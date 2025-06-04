package SalaDeEspera;

import Menu.Menu;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SalaDeEsperaController implements Initializable {

    @FXML Pane rootPane;
    @FXML ImageView fondoImage;
    @FXML GridPane contentPane;
    @FXML GridPane tablePane;
    @FXML ImageView textureImg;
    @FXML ImageView imageCargando;
    @FXML Label labelEsperando;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        javafx.application.Platform.runLater(() -> {
            Stage stage = (Stage) rootPane.getScene().getWindow();
            stage.setFullScreen(Menu.fullScreen);
        });

        // Adaptar el fondo a la resolución del dispositivo
        fondoImage.fitWidthProperty().bind(rootPane.widthProperty());
        fondoImage.fitHeightProperty().bind(rootPane.heightProperty());

        // Adaptar el contenido a la resolución del dispotivo
        contentPane.prefWidthProperty().bind(rootPane.widthProperty());
        contentPane.prefHeightProperty().bind(rootPane.heightProperty());

        // Adaptar la textura de la madera a la resolución del dispositivo
        contentPane.prefWidthProperty().bind(rootPane.widthProperty());
        contentPane.prefHeightProperty().bind(rootPane.heightProperty());

        textureImg.fitWidthProperty().bind(rootPane.widthProperty().divide(3));
        textureImg.fitHeightProperty().bind(rootPane.heightProperty().divide(1.5));

        imageCargando.fitWidthProperty().bind(rootPane.widthProperty().divide(5));
        imageCargando.fitHeightProperty().bind(rootPane.heightProperty().divide(5));

        Platform.runLater(() -> {
            double height = rootPane.getHeight();

            GridPane.setMargin(labelEsperando, new Insets(
                    height/19,
                    0,
                    0,
                    0
            ));
        });
    }

    public void salir(ActionEvent e) {
        try {
            Parent menuRoot = FXMLLoader.load(getClass().getResource("/Menu/Menu.fxml"));
            Scene scene = new Scene(menuRoot);
            scene.getStylesheets().add(getClass().getResource("/Menu/MenuStyles.css").toExternalForm());

            Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            stage.hide();
            stage.setScene(scene);
            stage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void opnenteEncontrado(ActionEvent e) throws IOException {
        Parent nuevoRoot = FXMLLoader.load(getClass().getResource("/Tablero/Tablero.fxml"));
        Scene nuevaScene = new Scene(nuevoRoot);
        nuevaScene.getStylesheets().add(getClass().getResource("/Tablero/TableroStyles.css").toExternalForm());

        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        stage.hide();
        stage.setScene(nuevaScene);
        stage.show();
    }
}