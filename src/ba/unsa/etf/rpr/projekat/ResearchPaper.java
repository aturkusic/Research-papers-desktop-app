package ba.unsa.etf.rpr.projekat;

import javafx.beans.property.SimpleStringProperty;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class ResearchPaper {
    private int id;
    private String researchPaperName;
    private String subject;
    private String[] keywords;
    private ArrayList<Author> authors;
    private SimpleStringProperty dateOfPublish = new SimpleStringProperty("");

    public ResearchPaper(int id, String resName, String subject, String[] keywords, ArrayList<Author> authors, LocalDate dateOfPublish) {
        this.id = id;
        this.researchPaperName = resName;
        this.authors = authors;
        this.subject = subject;
        this.keywords = keywords;
        this.setDateOfPublish(dateOfPublish);
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

    public ArrayList<Author> getAuthors() {
        return authors;
    }

    public void setAuthors(ArrayList<Author> authors) {
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

    public String getKeywordsAsString() {
        String str = "";
        int i = 0;
        for(String s : keywords) {
            if(i++ != keywords.length - 1)
                str += s + ",";
            else str += s;
        }
        return str;
    }

    public String getNamesOfAuthorsAsString() {
        String str = "";
        int i = 0;
        for (Author a : authors) {
            if (i++ != authors.size() - 1)
                str += a.getName() + " " + a.getSurname() + ",";
            else str += a.getName() + " " + a.getSurname();
        }
            return str;
    }

    //vrsim koverziju iz striga u LocalDate
    public LocalDate getDateOfPublish() {
        DateTimeFormatter dTF = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        LocalDate datum = LocalDate.now();
        if(!dateOfPublish.get().equals("")) {
            String anotherDate = dateOfPublish.getValue();
            datum = LocalDate.parse(anotherDate.replaceAll("\\s+",""), dTF);
        }
        return datum;
    }

    //konverzija iz LocalDate u string koji sacuvam u SympleStringProperty atribut
    public void setDateOfPublish(LocalDate dateOfPublish) {
        DateTimeFormatter dTF = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        String s = dTF.format(dateOfPublish);
        this.dateOfPublish.setValue(s);
    }
}
