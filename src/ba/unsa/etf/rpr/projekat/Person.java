package ba.unsa.etf.rpr.projekat;

public class Person {
    protected String name = "";
    protected String surname = "";

    public Person(String name, String surname) {
        this.name = name;
        this.surname = surname;
    }

    public Person() {}

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
}
