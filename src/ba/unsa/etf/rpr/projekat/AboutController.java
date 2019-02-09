package ba.unsa.etf.rpr.projekat;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class AboutController {
    public ImageView slika;
    public Label vrstaPrograma;

    @FXML
    public void initialize() {
        Image image = new Image("/img/lib.png");
        slika.setImage(image);
    }
}
