package ba.unsa.etf.rpr.projekat;

import java.io.Serializable;

public class Author implements Serializable {
    private int id;
    private String name = "";
    private String surname = "";
    private String title = "";
    private String university = "";


    public Author(int id, String name, String surname, String title, String uniAka) throws WrongAuthorDataException {
        if(!(nameSurnameTitleValidation(name) && nameSurnameTitleValidation(surname) && nameSurnameTitleValidation(title) && universityValidation(uniAka) && id >= 0)) throw new WrongAuthorDataException("Wrong info about author");
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.title = title;
        this.university = uniAka;

    }
    public Author(String name, String surname, String title, String uniAka) throws WrongAuthorDataException {
        if(!(nameSurnameTitleValidation(name) && nameSurnameTitleValidation(surname) && nameSurnameTitleValidation(title) && universityValidation(uniAka))) throw new WrongAuthorDataException("Wrong info about author");
        this.name = name;
        this.surname = surname;
        this.title = title;
        this.university = uniAka;
    }

    private boolean nameSurnameTitleValidation(String s) {
        return (!s.equals("") && !s.trim().isEmpty() && s.matches("[a-zA-Z]+"));
    }

    private boolean universityValidation(String s) {
        return (!s.equals("") && !s.trim().isEmpty());
    }


    public Author() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUniversity() {
        return university;
    }

    public void setUniversity(String university) {
        this.university = university;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
