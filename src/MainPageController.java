import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.media.AudioClip;
import javafx.stage.Stage;
import javafx.scene.effect.GaussianBlur;
import javafx.concurrent.Task;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class MainPageController {

    @FXML
    private BorderPane backgroundImage;

    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    private ImageView forestSoundImage;
    @FXML
    private Slider forestSoundSlider;

    @FXML
    private ImageView rainSoundImage;
    @FXML
    private Slider rainSoundSlider;

    @FXML
    private ImageView windSoundImage;
    @FXML
    private Slider windSoundSlider;

    @FXML
    private TableView<Mix> mixTable;
    @FXML
    private TableColumn<Mix, String> mixNameColumn;
    @FXML
    private TableColumn<Mix, Double> forestVolumeColumn;
    @FXML
    private TableColumn<Mix, Double> rainVolumeColumn;
    @FXML
    private TableColumn<Mix, Double> windVolumeColumn;

    @FXML
    private TextField mixNameField;
    
    @FXML
    private TextField hourField;
    
    @FXML
    private TextField minuteField;

    @FXML
    private VBox mixVBox;

    @FXML
    private HBox timerHBox;

    @FXML
    private Button startStopTimerButton;

    @FXML
    private Label countdownLabel;

    private AudioClip forestSound;
    private AudioClip rainSound;
    private AudioClip windSound;

    private boolean isPlaying = false;
    private boolean isTimerRunning = false;
    private Task<Void> timerTask;

    @FXML
    private Button imageButton;

    private boolean isPressed = false;

    @FXML
    private void switchToDocumentPage(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("FXMLDocument.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void initialize() {
        // Initialize table columns
        mixNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        forestVolumeColumn.setCellValueFactory(new PropertyValueFactory<>("forestVolume"));
        rainVolumeColumn.setCellValueFactory(new PropertyValueFactory<>("rainVolume"));
        windVolumeColumn.setCellValueFactory(new PropertyValueFactory<>("windVolume"));

        // Load existing mixes from XML
        loadMixesFromXML();

        // Load background image
        URL backgroundImageUrl = getClass().getResource("/images/background.jpg");
        if (backgroundImageUrl != null) {
            Image image = new Image(backgroundImageUrl.toExternalForm());
            ImageView imageView = new ImageView(image);
            imageView.fitWidthProperty().bind(backgroundImage.widthProperty());
            imageView.fitHeightProperty().bind(backgroundImage.heightProperty());
            imageView.setEffect(new GaussianBlur(20)); // Apply GaussianBlur with radius 20
            backgroundImage.getChildren().add(0, imageView);
        } else {
            System.err.println("Background image not found!");
        }

        // Load sounds
        forestSound = loadAudioClip("/sounds/forest.mp3");
        rainSound = loadAudioClip("/sounds/rain.mp3");
        windSound = loadAudioClip("/sounds/wind.mp3");

        // Bind sliders to volume controls
        if (forestSound != null) {
            forestSoundSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
                forestSound.setVolume(newVal.doubleValue() / 100);
                if (newVal.doubleValue() == 0 && isPlaying) {
                    forestSound.stop();
                } else if (newVal.doubleValue() > 0 && !forestSound.isPlaying() && isPlaying) {
                    forestSound.play();
                }
            });
        }
        if (rainSound != null) {
            rainSoundSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
                rainSound.setVolume(newVal.doubleValue() / 100);
                if (newVal.doubleValue() == 0 && isPlaying) {
                    rainSound.stop();
                } else if (newVal.doubleValue() > 0 && !rainSound.isPlaying() && isPlaying) {
                    rainSound.play();
                }
            });
        }
        if (windSound != null) {
            windSoundSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
                windSound.setVolume(newVal.doubleValue() / 100);
                if (newVal.doubleValue() == 0 && isPlaying) {
                    windSound.stop();
                } else if (newVal.doubleValue() > 0 && !windSound.isPlaying() && isPlaying) {
                    windSound.play();
                }
            });
        }

        // Add listener to TableView selection
        mixTable.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Mix>() {
            @Override
            public void changed(ObservableValue<? extends Mix> observable, Mix oldValue, Mix newValue) {
                if (newValue != null) {
                    playMix(newValue);
                }
            }
        });
    }

    private void loadImageIntoImageView(String resourcePath, ImageView imageView) {
        URL imageUrl = getClass().getResource(resourcePath);
        if (imageUrl != null) {
            imageView.setImage(new Image(imageUrl.toExternalForm()));
        } else {
            System.err.println("Image not found: " + resourcePath);
        }
    }

    private AudioClip loadAudioClip(String resourcePath) {
        URL audioUrl = getClass().getResource(resourcePath);
        if (audioUrl != null) {
            return new AudioClip(audioUrl.toExternalForm());
        } else {
            System.err.println("Audio clip not found: " + resourcePath);
            return null;
        }
    }

    @FXML
    private void togglePlayStop(ActionEvent event) {
        if (isPlaying) {
            stopAllSounds();
        } else {
            playAllSounds();
        }
        isPlaying = !isPlaying;
    }

    private void playAllSounds() {
        if (forestSound != null && forestSoundSlider.getValue() > 0) {
            forestSound.play();
        }
        if (rainSound != null && rainSoundSlider.getValue() > 0) {
            rainSound.play();
        }
        if (windSound != null && windSoundSlider.getValue() > 0) {
            windSound.play();
        }
    }

    private void stopAllSounds() {
        if (forestSound != null) forestSound.stop();
        if (rainSound != null) rainSound.stop();
        if (windSound != null) windSound.stop();
    }

    @FXML
    private void resetSounds(ActionEvent event) {
        stopAllSounds();
        forestSoundSlider.setValue(0);
        rainSoundSlider.setValue(0);
        windSoundSlider.setValue(0);
        isPlaying = false;
    }

    @FXML
    private void saveMix(ActionEvent event) {
        String name = mixNameField.getText().trim();
        if (name.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Mix name cannot be empty.", ButtonType.OK);
            alert.showAndWait();
            return;
        }

        try {
            // Create a new XML document
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.newDocument();

            // Root element
            Element rootElement = doc.createElement("Mix");
            doc.appendChild(rootElement);

            // Mix name element
            Element nameElement = doc.createElement("Name");
            nameElement.appendChild(doc.createTextNode(name));
            rootElement.appendChild(nameElement);

            // Forest sound element
            Element forestElement = doc.createElement("ForestSound");
            forestElement.appendChild(doc.createTextNode(Double.toString(forestSoundSlider.getValue())));
            rootElement.appendChild(forestElement);

            // Rain sound element
            Element rainElement = doc.createElement("RainSound");
            rainElement.appendChild(doc.createTextNode(Double.toString(rainSoundSlider.getValue())));
            rootElement.appendChild(rainElement);

            // Wind sound element
            Element windElement = doc.createElement("WindSound");
            windElement.appendChild(doc.createTextNode(Double.toString(windSoundSlider.getValue())));
            rootElement.appendChild(windElement);

            // Write the content into an XML file
            File file = new File("mixes/" + name + ".xml");
            file.getParentFile().mkdirs(); // Create directories if they do not exist
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult streamResult = new StreamResult(file);

            transformer.transform(source, streamResult);

            // Add the new mix to the table
            mixTable.getItems().add(new Mix(name, forestSoundSlider.getValue(), rainSoundSlider.getValue(), windSoundSlider.getValue()));

            System.out.println("Mix saved!");

        } catch (ParserConfigurationException | TransformerException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void deleteMix(ActionEvent event) {
        Mix selectedMix = mixTable.getSelectionModel().getSelectedItem();
        if (selectedMix != null) {
            File file = new File("mixes/" + selectedMix.getName() + ".xml");
            if (file.delete()) {
                mixTable.getItems().remove(selectedMix);
                System.out.println("Mix deleted!");
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to delete mix file.", ButtonType.OK);
                alert.showAndWait();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING, "No mix selected.", ButtonType.OK);
            alert.showAndWait();
        }
    }

    private void loadMixesFromXML() {
        File dir = new File("mixes");
        if (dir.exists() && dir.isDirectory()) {
            for (File file : dir.listFiles()) {
                if (file.isFile() && file.getName().endsWith(".xml")) {
                    try {
                        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                        Document doc = dBuilder.parse(file);
                        doc.getDocumentElement().normalize();

                        String name = doc.getElementsByTagName("Name").item(0).getTextContent();
                        double forestVolume = Double.parseDouble(doc.getElementsByTagName("ForestSound").item(0).getTextContent());
                        double rainVolume = Double.parseDouble(doc.getElementsByTagName("RainSound").item(0).getTextContent());
                        double windVolume = Double.parseDouble(doc.getElementsByTagName("WindSound").item(0).getTextContent());

                        mixTable.getItems().add(new Mix(name, forestVolume, rainVolume, windVolume));

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void playMix(Mix mix) {
        // Stop all sounds first
        stopAllSounds();

        // Set slider values based on the mix
        forestSoundSlider.setValue(mix.getForestVolume());
        rainSoundSlider.setValue(mix.getRainVolume());
        windSoundSlider.setValue(mix.getWindVolume());

        // Play the sounds
        if (forestSound != null && mix.getForestVolume() > 0) {
            forestSound.setVolume(mix.getForestVolume() / 100);
            forestSound.play();
        }
        if (rainSound != null && mix.getRainVolume() > 0) {
            rainSound.setVolume(mix.getRainVolume() / 100);
            rainSound.play();
        }
        if (windSound != null && mix.getWindVolume() > 0) {
            windSound.setVolume(mix.getWindVolume() / 100);
            windSound.play();
        }

        isPlaying = true;
    }

    @FXML
    private void startStopTimer(ActionEvent event) {
        if (isTimerRunning) {
            // Stop timer
            isTimerRunning = false;
            if (timerTask != null) {
                timerTask.cancel();
            }
            startStopTimerButton.setText("Start Timer");
        } else {
            // Start timer
            String hourText = hourField.getText().trim();
            String minuteText = minuteField.getText().trim();

            int hours = 0;
            int minutes = 0;

            try {
                if (!hourText.isEmpty()) {
                    hours = Integer.parseInt(hourText);
                }
                if (!minuteText.isEmpty()) {
                    minutes = Integer.parseInt(minuteText);
                }
            } catch (NumberFormatException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid time format. Please enter valid numbers.", ButtonType.OK);
                alert.showAndWait();
                return;
            }

            int totalSeconds = hours * 3600 + minutes * 60;
            if (totalSeconds <= 0) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Please enter a duration for the timer.", ButtonType.OK);
                alert.showAndWait();
                return;
            }

            isTimerRunning = true;
            startStopTimerButton.setText("Stop Timer");

            timerTask = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    for (int i = totalSeconds; i >= 0; i--) {
                        if (!isTimerRunning) {
                            break;
                        }
                        int currentHours = i / 3600;
                        int currentMinutes = (i % 3600) / 60;
                        int currentSeconds = i % 60;
                        updateMessage(String.format("%dh%dm%ds", currentHours, currentMinutes, currentSeconds));
                        Thread.sleep(1000);
                    }
                    return null;
                }

                @Override
                protected void succeeded() {
                    stopAllSounds();
                    isPlaying = false;
                    isTimerRunning = false;
                    startStopTimerButton.setText("Start Timer");
                    updateMessage("0h0m0s");
                }

                @Override
                protected void cancelled() {
                    isTimerRunning = false;
                    updateMessage("0h0m0s");
                }
            };

            countdownLabel.textProperty().bind(timerTask.messageProperty());
            new Thread(timerTask).start();
        }
    }

    @FXML
    private void toggleTimerVisibility(ActionEvent event) {
        timerHBox.setVisible(!timerHBox.isVisible());
    }

    @FXML
    private void toggleMixTableVisibility(ActionEvent event) {
        mixVBox.setVisible(!mixVBox.isVisible());
    }
    
}
