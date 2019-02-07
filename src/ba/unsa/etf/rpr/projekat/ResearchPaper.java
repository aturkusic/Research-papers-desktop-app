package ba.unsa.etf.rpr.projekat;

import java.sql.ResultSet;
import java.util.ArrayList;

public class ResearchPaper {
    private int id;
    private String researchPaperName = "";
    private String subject = "";
    private String[] keywords;
    private ArrayList<Autor> authors = new ArrayList<>();


    public ResearchPaper(int id, String resName, String subject, String[] keywords, ArrayList<Autor> authors) {
        this.id = id;
        this.researchPaperName = resName;
        this.authors = authors;
        this.subject = subject;
        this.keywords = keywords;
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

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String[] getKeywords() {
        return keywords;
    }

    public void setKeywords(String[] keywords) {
        this.keywords = keywords;
    }
}
