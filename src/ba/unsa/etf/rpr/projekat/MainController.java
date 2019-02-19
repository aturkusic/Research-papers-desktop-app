package ba.unsa.etf.rpr.projekat;


import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import net.sf.jasperreports.engine.JRException;

import java.io.*;
import java.util.ResourceBundle;
import java.util.function.Function;
import java.util.function.Predicate;

import static java.awt.Frame.NORMAL;
import static javafx.scene.control.PopupControl.USE_COMPUTED_SIZE;

public class MainController {

    public Button tbAddResearchPaper;
    public Button tbRemoveResearchPaper;
    public Button tbEditResearchPaper;
    public Tab ResearchPaperTab;
    public TableView<ResearchPaper> tableResearchPapers;
    public TableColumn colResearchPaperName;
    public TableColumn<ResearchPaper, String> colNameAuthor;
    public TextField searchField;
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
        tableResearchPapers.setRowFactory( tv -> {
            TableRow<ResearchPaper> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    ResearchPaper researchPaper = tableResearchPapers.getSelectionModel().getSelectedItem();
                    if(researchPaper == null) return;
                    Stage stage = new Stage();
                    Parent root = null;
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/doubleClick.fxml"));
                        DoubleClickController dCController = new DoubleClickController(dao, researchPaper);
                        loader.setController(dCController);
                        root = loader.load();
                        stage.setTitle("Preview");
                        stage.setScene(new Scene(root, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE));
                        stage.setResizable(false);
                        stage.showAndWait();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            return row;
        });
        new Thread(() -> {
            FilteredList<ResearchPaper> filteredList = new FilteredList<>(listRP, e -> true);
            searchField.setOnKeyPressed( e -> {
                searchField.textProperty().addListener((observableValue, o, n) -> {
                    filteredList.setPredicate((Predicate<? super ResearchPaper>) predicate -> {
                            if(n == null || n.isEmpty()) return true;
                            if(predicate.getResearchPaperName().toLowerCase().contains(n) || predicate.getResearchPaperName().contains(n)) return true;
                            else if(predicate.getSubject().toLowerCase().contains(n)) return true;
                            else if(predicate.getKeywordsAsString().toLowerCase().contains(n)) return true;
                            else if(predicate.getNamesOfAuthorsAsString().contains(n)) return true;
                            return false;
                    });
                });
                SortedList<ResearchPaper> sortedList = new SortedList<>(filteredList);
                sortedList.comparatorProperty().bind(tableResearchPapers.comparatorProperty());
                tableResearchPapers.setItems(sortedList);
            });
        }).start();


    }

    public void tbAddResearchPaperAction(ActionEvent actionEvent) {
        Stage stage = new Stage();
        Parent root = null;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/add.fxml"));
            AddController rPController = new AddController(dao, null);
            rPController.setController(this);
            loader.setController(rPController);
            root = loader.load();
            stage.setTitle("Add research paper");
            stage.setScene(new Scene(root, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE));
            stage.setResizable(false);
            stage.setOnHiding( event -> {
                listRP = dao.getResearchPapers();
                tableResearchPapers.setItems(listRP);
            } );
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void tbEditResearchPaperAction(ActionEvent actionEvent) {
        ResearchPaper researchPaper = tableResearchPapers.getSelectionModel().getSelectedItem();
        if(researchPaper == null) return;
        Stage stage = new Stage();
        Parent root = null;
        try {
            ResourceBundle bundle = ResourceBundle.getBundle("Translation");
            FXMLLoader loader = new FXMLLoader( getClass().getResource("/fxml/glavna.fxml" ), bundle);
            AddController rPController = new AddController(dao, researchPaper);
            rPController.setController(this);
            loader.setController(rPController);
            root = loader.load();
            stage.setTitle("Add research paper");
            stage.setScene(new Scene(root, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE));
            stage.setResizable(false);
            stage.setOnHiding( event -> {
                listRP = dao.getResearchPapers();
                tableResearchPapers.setItems(listRP);
            } );
            stage.showAndWait();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void aboutAction(ActionEvent actionEvent) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/about.fxml"));
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Stage pomocniProzor = new Stage();
        pomocniProzor.setTitle("About");
        pomocniProzor.resizableProperty().setValue(false);
        pomocniProzor.setScene(new Scene(root, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE));
        pomocniProzor.initModality(Modality.APPLICATION_MODAL);
        pomocniProzor.show();
    }

    @FXML
    public void printAction() {
        new Thread(() -> {
            try {
                new ResearchPaperReport().showReport(dao.getConn());
            } catch (JRException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @FXML
    public void exitAction(){
        System.exit(NORMAL);
    }

    @FXML
    public void saveAction(){
        ResearchPaper researchPaper = tableResearchPapers.getSelectionModel().getSelectedItem();
        if(researchPaper == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Select research paper from table!!!");
            alert.initStyle(StageStyle.UTILITY);
            alert.showAndWait();
            return;
        }
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Text file", "*.txt"),
                new FileChooser.ExtensionFilter("PDF file", "*.pdf"),
                new FileChooser.ExtensionFilter("Word file", "*.doc")
        );
        final Stage stage = new Stage();
        File selectedFile = fileChooser.showSaveDialog(stage);
        if (selectedFile != null) {
            doSave(selectedFile, researchPaper);
        }
    }

    public void doSave(File file, ResearchPaper researchPaper) {
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(file));
                writer.write(dao.getTextForResearchPaper(researchPaper));
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

    }

}
