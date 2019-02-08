package ba.unsa.etf.rpr.projekat;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class NewAuthorController {
    public Button okButton;
    public TextField authorNameField;
    public TextField authorSurnameField;
    public TextField authorTitleField;
    public TextField unyInstituteField;
    public Button cancelButton;
    ResearchPaperDAO dao;
    AddController addController;

    public NewAuthorController(ResearchPaperDAO dao, AddController addController) {
        this.dao = dao;
        this.addController = addController;
    }

    public void okButtonAction(ActionEvent actionEvent) {
    }

    public void cancelButtonAction(ActionEvent actionEvent) {
        cancelButton.getScene().getWindow().hide();
    }
}
