package ba.unsa.etf.rpr.projekat;

public class AddController {
   private MainController controller = null;
   ResearchPaperDAO dao = null;
   ResearchPaper researchPaper = null;

    public AddController(ResearchPaperDAO dao, ResearchPaper researchPaper) {
        this.dao = dao;
        this.researchPaper = researchPaper;
    }

    public MainController getController() {
        return controller;
    }

    public void setController(MainController controller) {
        this.controller = controller;
    }
}
