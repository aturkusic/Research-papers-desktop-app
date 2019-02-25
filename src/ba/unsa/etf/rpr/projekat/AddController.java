package ba.unsa.etf.rpr.projekat;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
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
    public Button fromXmlBtn;
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
        if(bundle.getLocale().toString().equals("bs")) keywordsField.setPromptText("Razdvojite zarezom");
        else keywordsField.setPromptText("Separate with comma");
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

        listAuthors.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent click) {
                if (click.getClickCount() == 2) {
                    Author author = listAuthors.getSelectionModel().getSelectedItem();
                    if(author == null) return;
                    Stage stage = new Stage();
                    Parent root = null;
                    try {
                        FXMLLoader loader = new FXMLLoader( getClass().getResource("/fxml/authorInfo.fxml" ), bundle);
                        AuthorInfoController authorController = new AuthorInfoController(author);
                        loader.setController(authorController);
                        root = loader.load();
                        if(bundle.getLocale().toString().equals("bs")) stage.setTitle("Informacije o autoru/ima");
                        else stage.setTitle("Information about author/s");
                        stage.setScene(new Scene(root, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE));
                        stage.setResizable(false);
                        stage.showAndWait();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        Tooltip tooltip = new Tooltip();
        if(bundle.getLocale().toString().equals("bs")) tooltip.setText("Drzite CTRL za selekciju vise autora.\nDvoklik za informacije o autoru.");
        else tooltip.setText("Hold CTRL for multiple selection.\nDoubleClik for author info.");
        listAuthors.setTooltip(tooltip);
        Tooltip t1 = new Tooltip();
        Tooltip t2 = new Tooltip();
        if(bundle.getLocale().toString().equals("bs")) t1.setText("Dodaj novog autora.");
        else t1.setText("Add new author.");
        if(bundle.getLocale().toString().equals("bs")) t2.setText("Dodaj novog autora preko XML fajla.");
        else t2.setText("Add new author with XML file.");
        addNewAuthorBtn.setTooltip(t1);
        fromXmlBtn.setTooltip(t2);
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
            String[] strings = keywordsField.getText().split(",");
            ArrayList<Author> autori = new ArrayList<>(listAuthors.getSelectionModel().getSelectedItems());
            ResearchPaper researchPaper = null;
            try {
                researchPaper = new ResearchPaper(0, resPaperNameField.getText(), subjectField.getText(), strings, autori, LocalDate.now());
            } catch (WrongResearchPaperDataException e) {
                e.printStackTrace();
            }
            for(ResearchPaper rp : dao.getResearchPapers())
                if(rp.equals(researchPaper)) return;
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

    //Ucitavanje iz xml fajla
    public void openXMLAuthor(File test) {
        Document xmldoc = null;
        try {
            DocumentBuilder docReader = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            xmldoc = docReader.parse(test);
        } catch (Exception e) {
            System.out.println("Nije validan XML dokument");
            return;
        }
        try {
            NodeList listaAuthors = xmldoc.getElementsByTagName("author");
            for (int i = 0; i < listaAuthors.getLength(); i++) {
                Node author = listaAuthors.item(i);
                if (author.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) author;
                    Author tmpAuthor = new Author();
                    tmpAuthor.setName(element.getElementsByTagName("name").item(0).getTextContent());
                    tmpAuthor.setSurname(element.getElementsByTagName("surname").item(0).getTextContent());
                    tmpAuthor.setTitle(element.getElementsByTagName("title").item(0).getTextContent());
                    tmpAuthor.setUniversity(element.getElementsByTagName("university").item(0).getTextContent());
                    dao.addAuthor(tmpAuthor);
                    listAuthors.getItems().add(tmpAuthor);
                    listAuthors.getSelectionModel().select(tmpAuthor);
                }
            }
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Pogrešan format datoteke");
            alert.setHeaderText("Neispravan format datoteke");
            alert.initStyle(StageStyle.UTILITY);
            alert.showAndWait();
        }
    }

    public void fromXMLAction(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("XML File", "*.xml"));
        final Stage stage = new Stage();
        File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null) {
            openXMLAuthor(selectedFile);
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
