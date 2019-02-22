package ba.unsa.etf.rpr.projekat;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;

public interface ResearchPaperDAO {

     private void regenerisiBazu() {}

     Author getAuthorById(int id);

     ObservableList<Author> getAuthors();

     ResearchPaper getResearchPaperById(int id);

     ObservableList<ResearchPaper> getResearchPapers();

     String getTextForResearchPaper(ResearchPaper researchPaper);

     void addResearchPaper(ResearchPaper researchPaper, String text);

     void updateResearchPaper(ResearchPaper researchPaper, String text);

}

