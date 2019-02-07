package ba.unsa.etf.rpr.projekat;

import java.sql.ResultSet;
import java.util.ArrayList;

public class ResearchPaper {
    private int id;
    private String researchPaperName = "";
    private ArrayList<Autor> authors = new ArrayList<>();


    public ResearchPaper(String resName, ArrayList<Autor> authors) {
        this.researchPaperName = resName;
        this.authors = authors;
    }

    public ResearchPaper(int id, String resName, ArrayList<Autor> authors) {
        this.id = id;
        this.researchPaperName = resName;
        this.authors = authors;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getResearchPaperName() {
        return researchPaperName;
    }

    public void setResearchPaperName(String researchPaperName) {
        this.researchPaperName = researchPaperName;
    }

    public ArrayList<Autor> getAuthors() {
        return authors;
    }

    public void setAuthors(ArrayList<Autor> authors) {
        this.authors = authors;
    }
}
