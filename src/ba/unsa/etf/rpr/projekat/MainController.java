package ba.unsa.etf.rpr.projekat;


import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

import static javafx.scene.control.PopupControl.USE_COMPUTED_SIZE;

public class MainController {

    public Button tbAddResearchPaper;
    public Button tbRemoveResearchPaper;
    public Button tbEditResearchPaper;
    public Tab ResearchPaperTab;
    public TableView tabelaResearchPapers;
    public TableColumn colResearchPaperName;
    public TableColumn colNameAuthor;
    public Button searchBtn;
    private ResearchPaperDAO dao;

    public MainController() {
        dao = dao.getInstance();
    }

    @FXML
    public void initialize(){

    }

    public void tbAddResearchPaperAction(ActionEvent actionEvent) {
    }

    public void tbChangeResearchPaperAction(ActionEvent actionEvent) {
    }

    public void tbEditResearchPaperAction(ActionEvent actionEvent) {
    }

    public void searchBtnAction(ActionEvent actionEvent) {
    }
}
