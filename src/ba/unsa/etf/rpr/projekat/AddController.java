package ba.unsa.etf.rpr.projekat;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

import static javafx.scene.control.PopupControl.USE_COMPUTED_SIZE;

public class AddController {
    public TextField resPaperNameField;
    public TextField subjectField;
    public TextField KeywordsField;
    public Button addNewAuthorBtn;
    public TextArea textArea;
    public Button okButton;
    public Button cancelButton;
    private MainController controller = null;
   ResearchPaperDAO dao = null;
   ResearchPaper researchPaper = null;

    public AddController(ResearchPaperDAO dao, ResearchPaper researchPaper) {
        this.dao = dao;
        this.researchPaper = researchPaper;
    }

    @FXML
    public void initialize() {
    }

    public MainController getController() {
        return controller;
    }

    public void setController(MainController controller) {
        this.controller = controller;
    }

    public void addNewAuthorBtnAction(ActionEvent actionEvent) {
        Stage stage = new Stage();
        Parent root = null;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/authorAdd.fxml"));
            NewAuthorController authorController = new NewAuthorController(dao, this);
            loader.setController(authorController);
            root = loader.load();
            stage.setTitle("Add author");
            stage.setScene(new Scene(root, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE));
            stage.setResizable(false);
            stage.showAndWait();

            stage.setOnHiding( event -> {

            } );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void okButtonAction(ActionEvent actionEvent) {
    }

    public void cancelButtonAction(ActionEvent actionEvent) {
        cancelButton.getScene().getWindow().hide();
    }
}
