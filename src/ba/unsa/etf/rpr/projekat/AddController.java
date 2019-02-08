package ba.unsa.etf.rpr.projekat;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.function.Function;

import static javafx.scene.control.PopupControl.USE_COMPUTED_SIZE;

public class AddController {
    public TextField resPaperNameField;
    public TextField subjectField;
    public TextField KeywordsField;
    public Button addNewAuthorBtn;
    public TextArea textArea;
    public Button okButton;
    public ListView<String> listAuthors;
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
        listAuthors.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        String str = "";
        var autori = dao.getAuthors();
        for(Author a : autori) listAuthors.getItems().add(a.getName() + " " + a.getSurname());
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

    //provjera koja ce radit za sve textfield
    private void inputValidation(TextField text, Function<String, Boolean> validacija) {
        if(text != null) {
            text.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observableValue, String o, String n) {
                    if (validacija.apply(n)) {
                        text.getStyleClass().removeAll("poljeNijeIspravno");
                        text.getStyleClass().add("poljeIspravno");
                    } else {
                        text.getStyleClass().removeAll("poljeIspravno");
                        text.getStyleClass().add("poljeNijeIspravno");
                    }
                }
            });
        }
    }
}
