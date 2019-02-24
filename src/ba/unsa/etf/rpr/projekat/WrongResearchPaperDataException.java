package ba.unsa.etf.rpr.projekat;

public class WrongResearchPaperDataException extends Exception {
    // Konstruktor
    public WrongResearchPaperDataException(String poruka) {
        super(poruka);
    }
}
