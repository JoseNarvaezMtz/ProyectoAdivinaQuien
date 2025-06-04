package Creditos;

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
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class CreditosController implements Initializable {
    @FXML
    private ImageView fondoImage;
    @FXML
    private Pane rootPane;
    @FXML
    private GridPane contentPane;

    @FXML
    Button buttonSalir;

    private Stage stage;
    private Scene scene;
    private Parent root;

    public void bottonSalir(ActionEvent e){
        try {
            Parent menuRoot = FXMLLoader.load(getClass().getResource("/Menu/Menu.fxml"));
            Scene scene = new Scene(menuRoot);
            scene.getStylesheets().add(getClass().getResource("/Menu/MenuStyles.css").toExternalForm());

            Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            stage.hide();
            stage.setScene(scene);
            stage.show();
        }catch (IOException e1){
            e1.printStackTrace();
        }
    }

    public void initialize(URL url, ResourceBundle resourceBundle) {
        javafx.application.Platform.runLater(() -> {
            Stage stage = (Stage) rootPane.getScene().getWindow();
            stage.setFullScreen(Menu.fullScreen);
        });

        // Adaptar el fondo a la resolución del dispositivo
        fondoImage.fitWidthProperty().bind(rootPane.widthProperty());
        fondoImage.fitHeightProperty().bind(rootPane.heightProperty());

        // Adaptar el contenido a la resolución del dispotivo mediante un margin
        Platform.runLater(() -> {
            double width = rootPane.getWidth();
            double height = rootPane.getHeight();

            StackPane.setMargin(contentPane, new Insets(
                    height/9,
                    width/6,
                    height/9,
                    width/6
            ));
        });

        buttonSalir.prefWidthProperty().bind(rootPane.widthProperty().divide(7));
        buttonSalir.prefHeightProperty().bind(rootPane.heightProperty().divide(12));
    }
}
