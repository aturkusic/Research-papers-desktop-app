package ba.unsa.etf.rpr.projekat;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ResourceBundle;

import static javafx.scene.control.PopupControl.USE_COMPUTED_SIZE;

public class DoubleClickController {
    public TextArea textPrevievArea;
    public Label paperNameLabel;
    public Label subjectLabel;
    public ListView<Author> authorsField;
    public Button closeBtn;
    private ResearchPaperDAO dao;
    private ResearchPaper researchPaper;
    private ResourceBundle bundle;

    public DoubleClickController(ResearchPaperDAO dao, ResearchPaper researchPaper, ResourceBundle bundle) {
        this.dao = dao;
        this.researchPaper = researchPaper;
        this.bundle = bundle;
    }

    @FXML
    public void initialize() {
        authorsField.getItems().addAll(researchPaper.getAuthors());
        authorsField.setCellFactory(param -> new ListCell<Author>() {
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
        authorsField.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent click) {
                if (click.getClickCount() == 2) {
                    Author author = authorsField.getSelectionModel().getSelectedItem();
                    if(author == null) return;
                    Stage stage = new Stage();
                    Parent root = null;
                    try {
                        FXMLLoader loader = new FXMLLoader( getClass().getResource("/fxml/authorInfo.fxml" ), bundle);
                        AuthorInfoController authorController = new AuthorInfoController(author);
                        loader.setController(authorController);
                        root = loader.load();
                        stage.setTitle("Information about author");
                        stage.setScene(new Scene(root, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE));
                        stage.setResizable(false);
                        stage.showAndWait();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        textPrevievArea.setEditable(false);
        paperNameLabel.setText(researchPaper.getResearchPaperName());
        subjectLabel.setText(researchPaper.getSubject());
        textPrevievArea.setText(dao.getTextForResearchPaper(researchPaper));
    }

    public void closeAction(ActionEvent actionEvent) {
        closeBtn.getScene().getWindow().hide();
    }
}
