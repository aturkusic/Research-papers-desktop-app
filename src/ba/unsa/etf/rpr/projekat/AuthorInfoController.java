package ba.unsa.etf.rpr.projekat;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class AuthorInfoController {
    public Label authorName;
    public Label authorSurname;
    public Label authorTitle;
    public Label authorUnyInst;
    public Button okButton;
    private Author author;
    public AuthorInfoController(Author author) {
        this.author = author;
    }

    @FXML
    public void initialize() {
        authorName.setText(author.getName());
        authorSurname.setText(author.getSurname());
        authorTitle.setText(author.getTitle());
        authorUnyInst.setText(author.getUniversity());
    }

    public void okBtnAction(ActionEvent actionEvent) {
        okButton.getScene().getWindow().hide();
    }
}
