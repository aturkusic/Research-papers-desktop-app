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
    public TextField keywordsField;
    public Button addNewAuthorBtn;
    public TextArea textArea;
    public Button okButton;
    public ListView<Author> listAuthors;
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
        if(researchPaper == null) {
            var autori = dao.getAuthors();
            for (Author a : autori) listAuthors.getItems().add(a);
        } else { // ovo je slucaj ako je izmjena postojeceg a ne dodavanje novog
            var autori = dao.getAuthors();
            for (Author a : autori) listAuthors.getItems().add(a);
            int i = 0;
            for(Author a : researchPaper.getAuthors()) {
                for(Author s : listAuthors.getItems()) {
                    if(a.getId() == s.getId()) listAuthors.getSelectionModel().select(i++);
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

    private boolean nameSubjectValidation(String s) {
        return (!s.equals("") && !s.trim().isEmpty() && s.matches("[a-zA-Z]+"));
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
