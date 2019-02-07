package ba.unsa.etf.rpr.projekat;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.*;
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
            addResearchPaper = conn.prepareStatement("insert into ResearchPapers values (?,?,?);");
            changeResearchPaper = conn.prepareStatement("update ResearchPapers set name=?, subject=? where id = ?;");
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
/*

    public ObservableList<Autor> getAuthors() {
        ObservableList<Autor> vlasnici = FXCollections.observableArrayList();
        try {
            ResultSet rs = getAllAuthor.executeQuery();
            while (rs.next()) {
                Autor autor = getVlasnikFromResultSet(rs);
                vlasnici.add(vlasnik);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return vlasnici;
    }
*/
}
