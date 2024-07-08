import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class InformasiAppController implements Initializable {

    @FXML
    private BorderPane backgroundImage; // Referensi ke BorderPane di FXML

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Memuat gambar latar belakang
        Image image = new Image(getClass().getResourceAsStream("/images/background.jpg"));

        // Mengatur gambar latar belakang menggunakan ImageView
        ImageView imageView = new ImageView(image);
        imageView.fitWidthProperty().bind(backgroundImage.widthProperty());
        imageView.fitHeightProperty().bind(backgroundImage.heightProperty());

        // Menambahkan imageView ke lapisan latar belakang BorderPane
        backgroundImage.getChildren().add(0, imageView); // Menambahkan sebagai anak pertama (lapisan belakang)
    }

    @FXML
    private void handleBackButtonAction(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("FXMLDocument.fxml"));
        Scene scene = new Scene(root);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
}
