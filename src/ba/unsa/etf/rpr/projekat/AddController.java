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
import javafx.stage.StageStyle;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.function.Function;

import static javafx.scene.control.PopupControl.USE_COMPUTED_SIZE;

public class AddController {
    public TextField resPaperNameField;
    public TextField subjectField;
    public TextField keywordsField;
    public Button addNewAuthorBtn;
    public TextArea textArea;
    public Button okButton;
    public ListView<Author> listAuthors;
    public Button cancelButton;
    private MainController controller = null;
    private ResearchPaperDAOBaza dao;
    private ResearchPaper researchPaper;
    private  ResourceBundle bundle;
    private enum Purpose {ADD, EDIT}
    private Purpose purpose;

    public AddController(ResearchPaperDAOBaza dao, ResearchPaper researchPaper, ResourceBundle bundle) {
        this.dao = dao;
        this.researchPaper = researchPaper;
        this.bundle = bundle;
        if(researchPaper == null) purpose = Purpose.ADD;
        else purpose = Purpose.EDIT;
    }

    @FXML
    public void initialize() {
        keywordsField.setPromptText("Separate with comma");
        listAuthors.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        listAuthors.setCellFactory(param -> new ListCell<Author>() {
            @Override
            protected void updateItem(Author item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null || item.getName() == null || item.getSurname() == null) {
                    setText(null);
                } else {
                    setText(item.getName() + " " + item.getSurname());
                }
            }
        });
        if(purpose == Purpose.ADD) {
            var autori = dao.getAuthors();
            for (Author a : autori) listAuthors.getItems().add(a);
        } else { // ovo je slucaj ako je izmjena postojeceg a ne dodavanje novog
            var autori = dao.getAuthors();
            for (Author a : autori) listAuthors.getItems().add(a);
            int i = 0;
            for(Author a : researchPaper.getAuthors()) {
                for(Author s : listAuthors.getItems()) {
                    if(a.getId() == s.getId()) listAuthors.getSelectionModel().select(s);
                }
                i = 0;
            }
            resPaperNameField.setText(researchPaper.getResearchPaperName());
            subjectField.setText(researchPaper.getSubject());
            String str = "";
            i = 0;
            for(String s : researchPaper.getKeywords()) {
                if(i++ != researchPaper.getKeywords().length - 1)
                    str += s + ",";
                else str += s;
            }
            keywordsField.setText(str);
            textArea.setText(dao.getTextForResearchPaper(researchPaper));
        }
        inputValidation(resPaperNameField, this::nameSubjectValidation);
        inputValidation(subjectField, this::nameSubjectValidation);
        inputValidation(keywordsField, this::keywordsValidation);
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
            FXMLLoader loader = new FXMLLoader( getClass().getResource("/fxml/authorAdd.fxml" ), bundle);
            NewAuthorController authorController = new NewAuthorController(dao, this);
            loader.setController(authorController);
            root = loader.load();
            if(bundle.getLocale().toString().equals("bs")) stage.setTitle("Dodaj autora");
            else stage.setTitle("Add author");
            stage.setScene(new Scene(root, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE));
            stage.setResizable(false);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void okButtonAction(ActionEvent actionEvent) {
        //ako je dodavanje novog
        if(nameSubjectValidation(resPaperNameField.getText()) && nameSubjectValidation(subjectField.getText()) && nameSubjectValidation(keywordsField.getText()) && listAuthors.getSelectionModel().getSelectedItem() != null && purpose == Purpose.ADD) {
            String[] strings =keywordsField.getText().split(",");
            ArrayList<Author> autori = new ArrayList<>(listAuthors.getSelectionModel().getSelectedItems());
            ResearchPaper researchPaper = new ResearchPaper(0, resPaperNameField.getText(), subjectField.getText(), strings, autori, LocalDate.now());
            dao.addResearchPaper(researchPaper, textArea.getText());
            okButton.getScene().getWindow().hide();
            //ako je izmjena starog
        } else if(nameSubjectValidation(resPaperNameField.getText()) && nameSubjectValidation(subjectField.getText()) && nameSubjectValidation(keywordsField.getText()) && listAuthors.getSelectionModel().getSelectedItem() != null) {
            ArrayList<Author> autori = new ArrayList<>(listAuthors.getSelectionModel().getSelectedItems());
            String[] strings =keywordsField.getText().split(",");
            researchPaper.setResearchPaperName(resPaperNameField.getText());
            researchPaper.setSubject(subjectField.getText());
            researchPaper.setKeywords(strings);
            researchPaper.setAuthors(autori);
            dao.updateResearchPaper(researchPaper, textArea.getText());
            okButton.getScene().getWindow().hide();
        } else if(listAuthors.getSelectionModel().getSelectedItem() == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("No authors selected!!!");
            alert.initStyle(StageStyle.UTILITY);
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Invalid fields!!!");
            alert.initStyle(StageStyle.UTILITY);
            alert.showAndWait();
        }
    }

    public void cancelButtonAction(ActionEvent actionEvent) {
        cancelButton.getScene().getWindow().hide();
    }

    private boolean nameSubjectValidation(String s) {
        return (!s.equals("") && !s.trim().isEmpty() && !s.matches(".*\\d+.*"));
    }

    private boolean keywordsValidation(String s) {
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
