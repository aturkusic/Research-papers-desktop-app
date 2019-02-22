package ba.unsa.etf.rpr.projekat;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;

public class ResearchPaperDAOBaza implements ResearchPaperDAO{
    private static ResearchPaperDAOBaza instanca;
    private PreparedStatement getAllResearchPaper;
    private PreparedStatement getAllAuthor;
    private PreparedStatement addAuthor;
    private PreparedStatement addResearchPaper;
    private PreparedStatement changeAuthor;
    private PreparedStatement changeResearchPaper;
    private PreparedStatement getNewIdAuthor;
    private PreparedStatement getNewIdResearchPaper;
    private Connection conn;

    public static ResearchPaperDAOBaza getInstance() {
        if (instanca == null) instanca = new ResearchPaperDAOBaza();
        return instanca;
    }

    // konstruktor
    public ResearchPaperDAOBaza() {
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
            addResearchPaper = conn.prepareStatement("insert into ResearchPapers values (?,?,?,?,?,?);");
            changeResearchPaper = conn.prepareStatement("update ResearchPapers set name=?, subject=?, keywords=?, text=?, datum_objavljivanja = ? where id = ?;");
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
                return new ResearchPaper(rs.getInt(1), rs.getString(2), rs.getString(3), strings, autori, rs.getDate(6).toLocalDate());
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

    public void addAuthor(Author author) {
        ResultSet rs = null;
        try {
            rs = getNewIdAuthor.executeQuery();
            int id = 0;
            if (rs.next()) id = rs.getInt(1);
            author.setId(id);
            addAuthor.setInt(1, author.getId());
            addAuthor.setString(2, author.getName());
            addAuthor.setString(3, author.getSurname());
            addAuthor.setString(4, author.getTitle());
            addAuthor.setString(5, author.getUniversity());
            addAuthor.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getTextForResearchPaper(ResearchPaper researchPaper) {
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT text from ResearchPapers where id = ?;");
            ps.setInt(1 , researchPaper.getId());
            ResultSet rs = ps.executeQuery();
            if(rs.next()) {
                return rs.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void addResearchPaper(ResearchPaper researchPaper, String text) {
        ResultSet rs = null;
        try {
            rs = getNewIdResearchPaper.executeQuery();
            int id = 0;
            if (rs.next()) id = rs.getInt(1);
            researchPaper.setId(id);
            addResearchPaper.setInt(1, researchPaper.getId());
            addResearchPaper.setString(2, researchPaper.getResearchPaperName());
            addResearchPaper.setString(3, researchPaper.getSubject());
            addResearchPaper.setString(4, researchPaper.getKeywordsAsString());
            addResearchPaper.setString(5, text);
            addResearchPaper.setDate(6, java.sql.Date.valueOf(LocalDate.now()));
            addResearchPaper.executeUpdate();
            PreparedStatement ps = conn.prepareStatement("insert into ResearchPapers_Authors values (?,?);");
            for(Author a : researchPaper.getAuthors()) {
                ps.setInt(1, researchPaper.getId());
                ps.setInt(2, a.getId());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateResearchPaper(ResearchPaper researchPaper, String text) {
        try {
            PreparedStatement ps = conn.prepareStatement("delete from ResearchPapers_Authors where researchP_id = ?;");
            ps.setInt(1, researchPaper.getId());
            ps.executeUpdate();
            changeResearchPaper.setString(1, researchPaper.getResearchPaperName());
            changeResearchPaper.setString(2, researchPaper.getSubject());
            changeResearchPaper.setString(3, researchPaper.getKeywordsAsString());
            changeResearchPaper.setString(4, text);
            changeResearchPaper.setDate(5, java.sql.Date.valueOf(LocalDate.now()));
            changeResearchPaper.setInt(5, researchPaper.getId());
            changeResearchPaper.executeUpdate();
            ps = conn.prepareStatement("insert into ResearchPapers_Authors values (?,?);");
            for(Author a : researchPaper.getAuthors()) {
                ps.setInt(1, researchPaper.getId());
                ps.setInt(2, a.getId());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public ArrayList<String> getAllResearchPapersFromAuthor(int id) {
        ArrayList<String> lista = new ArrayList<>();
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT rp.name from ResearchPapers rp, ResearchPapers_Authors rpa where rpa.researchP_id = rp.id and rpa.author_id = ?;");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) lista.add(rs.getString(1));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public Connection getConn() {
        return conn;
    }
}
