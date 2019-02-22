package ba.unsa.etf.rpr.projekat;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.StageStyle;

import java.util.function.Function;

public class NewAuthorController {
    public Button okButton;
    public TextField authorNameField;
    public TextField authorSurnameField;
    public TextField authorTitleField;
    public TextField unyInstituteField;
    public Button cancelButton;
    private ResearchPaperDAOBaza dao;
    private AddController addController;

    public NewAuthorController(ResearchPaperDAOBaza dao, AddController addController) {
        this.dao = dao;
        this.addController = addController;
    }

    @FXML
    public void initialize() {

        inputValidation(authorNameField, this::nameSurnameTitleValidation);
        inputValidation(authorSurnameField, this::nameSurnameTitleValidation);
        inputValidation(authorTitleField, this::nameSurnameTitleValidation);
        inputValidation(unyInstituteField, this::universityValidation);
    }

    public void okButtonAction(ActionEvent actionEvent) {
        if(nameSurnameTitleValidation(authorNameField.getText()) && nameSurnameTitleValidation(authorSurnameField.getText()) &&nameSurnameTitleValidation(authorTitleField.getText()) && universityValidation(unyInstituteField.getText())) {
            Author author = new Author(0, authorNameField.getText(), authorSurnameField.getText(), authorTitleField.getText(), unyInstituteField.getText());
            dao.addAuthor(author);
            addController.listAuthors.getItems().add(author);
            okButton.getScene().getWindow().hide();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Invalid data!!!");
            alert.initStyle(StageStyle.UTILITY);
            alert.showAndWait();
        }
    }

    public void cancelButtonAction(ActionEvent actionEvent) {
        cancelButton.getScene().getWindow().hide();
    }

    private boolean nameSurnameTitleValidation(String s) {
        return (!s.equals("") && !s.trim().isEmpty() && s.matches("[a-zA-Z]+"));
    }

    private boolean universityValidation(String s) {
        return (!s.equals("") && !s.trim().isEmpty());
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
