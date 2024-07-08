import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class FXMLDocumentController implements Initializable {

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
    private void handlePlayButtonAction() {
        try {
            // Memuat file FXML untuk scene baru (MainPage.fxml)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("MainPage.fxml"));
            Parent mainPageParent = loader.load();

            // Mendapatkan stage saat ini
            Stage stage = (Stage) backgroundImage.getScene().getWindow();

            // Mengatur scene baru pada stage
            Scene scene = new Scene(mainPageParent);
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            // Menangani pengecualian dengan benar
        }
    }

    @FXML
    private void handleInfoButtonAction() {
        try {
            // Memuat file FXML untuk scene informasi (informasiApp.fxml)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("informasiApp.fxml"));
            Parent informasiAppParent = loader.load();

            // Mendapatkan stage saat ini
            Stage stage = (Stage) backgroundImage.getScene().getWindow();

            // Mengatur scene baru pada stage
            Scene scene = new Scene(informasiAppParent);
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            // Menangani pengecualian dengan benar
        }
    }

    @FXML
    private Button exitButton;

    @FXML
    private void handleExitButtonAction(ActionEvent event) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Konfirmasi Keluar");
        alert.setHeaderText("Anda ingin keluar dari aplikasi?");
        alert.setContentText("Pilih Ya untuk keluar, atau Tidak untuk tetap di aplikasi.");

        // Tambahkan stylesheet
        alert.getDialogPane().getStylesheets().add(getClass().getResource("alert.css").toExternalForm());

        ButtonType buttonTypeYes = new ButtonType("Ya");
        ButtonType buttonTypeNo = new ButtonType("Tidak");

        alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == buttonTypeYes){
            Platform.exit();
            System.exit(0); // Optional, to ensure complete termination
        } else {
            // Tetap di aplikasi
            alert.close();
        }
    }
}
