package ba.unsa.etf.rpr.projekat;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

public class ResearchPaperDAO {
    private static ResearchPaperDAO instanca;
    private PreparedStatement getAllResearchPaper;
    private PreparedStatement getAllAuthor;
    private PreparedStatement addAuthor;
    private PreparedStatement addResearchPaper;
    private PreparedStatement changeAuthor;
    private PreparedStatement changeResearchPaper;
    private PreparedStatement getNewIdAuthor;
    private PreparedStatement getNewIdResearchPaper;
    private Connection conn;

    public static ResearchPaperDAO getInstance() {
        if (instanca == null) instanca = new ResearchPaperDAO();
        return instanca;
    }

    // konstruktor
    public ResearchPaperDAO() {
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:researchPapers.db");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            PreparedStatement ps = conn.prepareStatement("select * from author;");
        } catch (SQLException e) {
            regenerisiBazu();
            try {
                PreparedStatement ps = conn.prepareStatement("select * from author;");
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }

        try {
            getAllAuthor = conn.prepareStatement("select * from author order by id;");
            getAllResearchPaper = conn.prepareStatement("select * from ResearchPapers order by id;");
            addAuthor = conn.prepareStatement("insert into author values (?,?,?,?,?);");
            changeAuthor = conn.prepareStatement("update author set name=?, surname=?, title=?, university=? where id = ?");
            addResearchPaper = conn.prepareStatement("insert into ResearchPapers values (?,?,?,?,?);");
            changeResearchPaper = conn.prepareStatement("update ResearchPapers set name=?, subject=?, keywords=?, text=? where id = ?;");
            getNewIdAuthor = conn.prepareStatement("SELECT MAX(id)+1 FROM author;");
            getNewIdResearchPaper = conn.prepareStatement("SELECT MAX(id)+1 FROM ResearchPapers;");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //regenerise bazu u slucaju da nema tabela
    private void regenerisiBazu() {
        Scanner ulaz = null;
        try {
            ulaz = new Scanner(new FileInputStream("researchPapers.sql"));
            String sqlUpit = "";
            while (ulaz.hasNext()) {
                sqlUpit += ulaz.nextLine();
                if ( sqlUpit.charAt( sqlUpit.length()-1 ) == ';') {
                    try {
                        Statement stmt = conn.createStatement();
                        stmt.execute(sqlUpit);
                        sqlUpit = "";
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
            ulaz.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Author getAuthorById(int id) {
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement("select * from author where id = ?");
            ps.setInt(1,id);
            ResultSet resultSet = ps.executeQuery();
            if (resultSet.next()) {
                //ako ima vracamo odmah
                Author v = new Author(resultSet.getInt(1), resultSet.getString(2), resultSet.getString(3), resultSet.getString(4),
                        resultSet.getString(5));

                return v;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //nije pronadjen
        return null;
    }

    public ObservableList<Author> getAuthors() {
        ObservableList<Author> vlasnici = FXCollections.observableArrayList();
        try {
            ResultSet rs = getAllAuthor.executeQuery();
            while (rs.next()) {
                Author author = getAuthorById(rs.getInt(1));
                vlasnici.add(author);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return vlasnici;
    }

    public ResearchPaper getResearchPaperById(int id) {
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement("select * from ResearchPapers where id = ?");
            ps.setInt(1,id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                ArrayList<Author> autori = new ArrayList<>();
                PreparedStatement ps1 = conn.prepareStatement("select author_id from ResearchPapers_Authors where researchP_id = ?;");
                ps1.setInt(1, rs.getInt(1));
                ResultSet rs2 = ps1.executeQuery();
                while (rs2.next()) autori.add(getAuthorById(rs2.getInt(1)));
                String[] strings = rs.getString(4).split(",");
                return new ResearchPaper(rs.getInt(1), rs.getString(2), rs.getString(3), strings, autori);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //nije pronadjen
        return null;
    }

    public ObservableList<ResearchPaper> getResearchPapers() {
        ObservableList<ResearchPaper> researchPapers = FXCollections.observableArrayList();
        try {
            ResultSet rs = getAllResearchPaper.executeQuery();
            while (rs.next()) {
                researchPapers.add(getResearchPaperById(rs.getInt(1)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return researchPapers;
    }




}
