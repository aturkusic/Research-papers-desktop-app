package ba.unsa.etf.rpr.projekat;


import javafx.beans.property.SimpleStringProperty;
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
import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.function.Predicate;

import static java.awt.Frame.NORMAL;
import static javafx.scene.control.PopupControl.USE_COMPUTED_SIZE;

public class MainController {

    public Button tbAddResearchPaper;
    public Button tbEditResearchPaper;
    public Tab ResearchPaperTab;
    public Tab authorsTab;
    public TableView<ResearchPaper> tableResearchPapers;
    public TableView<Author> tableAuthors;
    public TableColumn colResearchPaperName;
    public TableColumn<ResearchPaper, String> colNameAuthor;
    public TextField searchField;
    public TableColumn colSubject;
    public TableColumn dateOfPublish;
    public CheckMenuItem engLanguage;
    public CheckMenuItem bsLanguage;
    public TextField searchFieldAuthors;
    public TableColumn<Author, String> colName;
    public TableColumn<Author, String> colResPapers;
    public TableColumn colUnyAuthor;
    public TableColumn colTitleAuthor;
    private ResearchPaperDAOBaza dao;
    private ObservableList<ResearchPaper> listRP;
    private ObservableList<Author> listAuthors;
    private ResourceBundle bundle;


    public MainController() {
        dao = dao.getInstance();
        listRP = dao.getResearchPapers();
        listAuthors = dao.getAuthors();
    }

    public MainController(ResourceBundle bundle) {
        this.bundle = bundle;
        dao = dao.getInstance();
        listRP = dao.getResearchPapers();
        listAuthors = dao.getAuthors();
    }

    @FXML
    public void initialize(){
        tableResearchPapers.setItems(listRP);
        tableAuthors.setItems(listAuthors);
        //Postavljanje vrijednosti kolona
        colResearchPaperName.setCellValueFactory(new PropertyValueFactory("researchPaperName"));
        colSubject.setCellValueFactory(new PropertyValueFactory("subject"));
        dateOfPublish.setCellValueFactory(new PropertyValueFactory<>("dateOfPublish"));
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
        colResPapers.setCellValueFactory((data) -> {
            String str = "";
            ArrayList<String> lista = dao.getAllResearchPapersFromAuthor(data.getValue().getId());
            int i = 0;
            for(String s : lista) {
                if(i++ != lista.size() - 1)
                    str += s + ", ";
                else str += s;
            }
            SimpleStringProperty property = new SimpleStringProperty(str);
            return property;
        });
        colTitleAuthor.setCellValueFactory(new PropertyValueFactory("title"));
        colUnyAuthor.setCellValueFactory(new PropertyValueFactory("university"));
        colName.setCellValueFactory((data) -> {
            String str = "";
            str = data.getValue().getName() + " " + data.getValue().getSurname();
            SimpleStringProperty property = new SimpleStringProperty(str);
            return property;
        });
        //Dvoklik akcija
        tableResearchPapers.setRowFactory( tv -> {
            TableRow<ResearchPaper> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    ResearchPaper researchPaper = tableResearchPapers.getSelectionModel().getSelectedItem();
                    if(researchPaper == null) return;
                    Stage stage = new Stage();
                    Parent root = null;
                    try {
                        FXMLLoader loader = new FXMLLoader( getClass().getResource("/fxml/doubleClick.fxml" ), bundle);
                        DoubleClickController dCController = new DoubleClickController(dao, researchPaper, bundle);
                        loader.setController(dCController);
                        root = loader.load();
                        if(bundle.getLocale().toString().equals("bs")) stage.setTitle("Pregled");
                        else stage.setTitle("Preview");
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
                            if(predicate.getResearchPaperName().toLowerCase().contains(n.toLowerCase())) return true;
                            else if(predicate.getSubject().toLowerCase().contains(n.toLowerCase())) return true;
                            else if(predicate.getKeywordsAsString().toLowerCase().contains(n.toLowerCase())) return true;
                            else if(predicate.getNamesOfAuthorsAsString().contains(n.toLowerCase())) return true;
                            return false;
                    });
                });
                SortedList<ResearchPaper> sortedList = new SortedList<>(filteredList);
                sortedList.comparatorProperty().bind(tableResearchPapers.comparatorProperty());
                tableResearchPapers.setItems(sortedList);
            });
        }).start();
        //Pretraga za autora
        new Thread(() -> {
            FilteredList<Author> filteredList = new FilteredList<>(listAuthors, e -> true);
            searchFieldAuthors.setOnKeyPressed( e -> {
                searchFieldAuthors.textProperty().addListener((observableValue, o, n) -> {
                    filteredList.setPredicate((Predicate<? super Author>) predicate -> {
                        if(n == null || n.isEmpty()) return true;
                        if(predicate.getName().toLowerCase().contains(n.toLowerCase())) return true;
                        if(predicate.getSurname().toLowerCase().contains(n) || predicate.getSurname().contains(n.toLowerCase())) return true;
                        else if(predicate.getTitle().toLowerCase().contains(n.toLowerCase())) return true;
                        else if(predicate.getUniversity().toLowerCase().contains(n.toLowerCase())) return true;
                        else {
                            var lista = dao.getAllResearchPapersFromAuthor(predicate.getId());
                            for(var x : lista) {
                                if(x.toLowerCase().contains(n.toLowerCase())) return true;
                            }
                        }
                        return false;
                    });
                });
                SortedList<Author> sortedList = new SortedList<>(filteredList);
                sortedList.comparatorProperty().bind(tableAuthors.comparatorProperty());
                tableAuthors.setItems(sortedList);
            });
        }).start();
        engLanguage.selectedProperty().setValue(true);

    }

    public void tbAddResearchPaperAction(ActionEvent actionEvent) {
        Stage stage = new Stage();
        Parent root = null;
        try {
            FXMLLoader loader = new FXMLLoader( getClass().getResource("/fxml/add.fxml" ), bundle);
            AddController addController = new AddController(dao, null, bundle);
            addController.setController(this);
            loader.setController(addController);
            root = loader.load();
            if(bundle.getLocale().toString().equals("bs")) stage.setTitle("Dodaj naucni rad");
            else stage.setTitle("Add research paper");
            stage.setScene(new Scene(root, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE));
            stage.setResizable(false);
            stage.setOnHiding( event -> {
                listRP = dao.getResearchPapers();
                tableResearchPapers.setItems(listRP);
                listAuthors = dao.getAuthors();
                tableAuthors.setItems(listAuthors);
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
            FXMLLoader loader = new FXMLLoader( getClass().getResource("/fxml/add.fxml" ), bundle);
            AddController editController = new AddController(dao, researchPaper, bundle);
            editController.setController(this);
            loader.setController(editController);
            root = loader.load();
            if(bundle.getLocale().toString().equals("bs")) stage.setTitle("Izmijeni naucni rad");
            else stage.setTitle("Edit research paper");
            stage.setScene(new Scene(root, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE));
            stage.setResizable(false);
            stage.setOnHiding( event -> {
                listRP = dao.getResearchPapers();
                tableResearchPapers.setItems(listRP);
                listAuthors = dao.getAuthors();
                tableAuthors.setItems(listAuthors);
            } );
            stage.showAndWait();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void aboutAction(ActionEvent actionEvent) {
        FXMLLoader loader = new FXMLLoader( getClass().getResource("/fxml/about.fxml" ), bundle);
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Stage pomocniProzor = new Stage();
        if(bundle.getLocale().toString().equals("bs")) pomocniProzor.setTitle("O aplikaciji");
        else pomocniProzor.setTitle("About");
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
    public void exitAction() {
        Alert info = new Alert(Alert.AlertType.CONFIRMATION);
        if(bundle.getLocale().toString().equals("bs")) info.setHeaderText("Da li sigurno zelite izaci?");
        else info.setHeaderText("Are you sure you want to exit?");
        info.showAndWait();
        if (info.getResult() == ButtonType.OK) {
            info.close();
            System.exit(NORMAL);
        }
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

    public void bosanskiAction(ActionEvent actionEvent) {
        engLanguage.selectedProperty().setValue(false);
        bsLanguage.selectedProperty().setValue(true);
        Locale.setDefault(new Locale("bs", "BA"));
        reloadScene(false , true);
    }

    private void reloadScene(boolean eng, boolean bs) {
        bundle = ResourceBundle.getBundle("Translation");
        Scene scene = tableResearchPapers.getScene();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/glavna.fxml"), bundle);
        loader.setController(this);
        try {
            scene.setRoot(loader.load());
            engLanguage.selectedProperty().setValue(eng);
            bsLanguage.selectedProperty().setValue(bs);
        } catch (IOException ignored) {

        }
    }

    public void engAction(ActionEvent actionEvent) {
        Locale.setDefault(new Locale("eng", "ENG"));
        reloadScene(true, false);
    }

}
