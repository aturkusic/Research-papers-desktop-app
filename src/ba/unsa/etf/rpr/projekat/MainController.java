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
    public TableView<ResearchPaper> tableResearchPapers;
    public TableColumn colResearchPaperName;
    public TableColumn<ResearchPaper, String> colNameAuthor;
    public Button searchBtn;
    public TableColumn colSubject;
    private ResearchPaperDAO dao;
    private ObservableList<ResearchPaper> listRP;

    public MainController() {
        dao = dao.getInstance();
        listRP = dao.getResearchPapers();
    }

    @FXML
    public void initialize(){
        tableResearchPapers.setItems(listRP);
        colResearchPaperName.setCellValueFactory(new PropertyValueFactory("researchPaperName"));
        colSubject.setCellValueFactory(new PropertyValueFactory("subject"));
        colNameAuthor.setCellValueFactory((data) -> {
            String str = "";
            var authors = data.getValue().getAuthors();
            int i = 0;
            for(Author s : authors) {
                if(i++ != authors.size() - 1)
                    str += s.getName() + " " + s.getSurname() + ",";
                else str += s.getName() + " " + s.getSurname();
            }
            SimpleStringProperty property = new SimpleStringProperty(str);
            return property;
            });

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
