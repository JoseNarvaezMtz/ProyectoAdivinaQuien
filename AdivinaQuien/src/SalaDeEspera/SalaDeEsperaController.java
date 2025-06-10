package SalaDeEspera;

import Menu.Menu;
import Sockets.Cliente;
import Tablero.TableroController;
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
    @FXML GridPane contentPane;
    @FXML GridPane tablePane;

    @FXML ImageView fondoImage;
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

        // Adaptar el tamaño de las imágenes a la resolución del dispositivo
        textureImg.fitWidthProperty().bind(rootPane.widthProperty().divide(3));
        textureImg.fitHeightProperty().bind(rootPane.heightProperty().divide(1.5));

        // Adaptar el gif animado que muestra mientras se conecta al servidor con la resolución del dispositivo
        imageCargando.fitWidthProperty().bind(rootPane.widthProperty().divide(5));
        imageCargando.fitHeightProperty().bind(rootPane.heightProperty().divide(5));

        // Es un método de JavaFX que permite ejecutar código en el hilo principal de la interfaz de usuario
        Platform.runLater(() -> {
            double height = rootPane.getHeight();

            GridPane.setMargin(labelEsperando, new Insets(
                    height/19,
                    0,
                    0,
                    0
            ));
        });

        // Hacemos las conexiones al servidor para pasar al tablero
        try {
            Menu.cliente = new Cliente("localhost", 5000, new Cliente.ClienteListener() {
                @Override
                public void onIniciarPartida() {
                    System.out.println("Iniciando partida");
                    Platform.runLater(() -> irTablero());
                }
            });
        } catch (IOException ex) {
            System.out.println("Error al conectar al servidor: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    // Método para regresar al Menú principal
    public void salir(ActionEvent e) {
        try {
            // Cerramos la conexion del cliente si existe
            if (Menu.cliente != null) {
                System.out.println("Desconectando al cliente");
                Menu.cliente.desconexion();
                Menu.cliente = null;
            }

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

    // Manda al usuario al tablero cuando se encuentra el oponente
    public void opnenteEncontrado(ActionEvent e) throws IOException {
       irTablero();
    }

    // Carga el tablero cuando se encuentra el oponente
    public void irTablero(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Tablero/Tablero.fxml")); // Crea el FXMLLoader
            Parent nuevoRoot = loader.load();
            //Parent nuevoRoot = FXMLLoader.load(getClass().getResource("/Tablero/Tablero.fxml"));
            Scene nuevaScene = new Scene(nuevoRoot);

            TableroController tableroController = loader.getController();
            if (Menu.cliente != null) {
                tableroController.setCliente(Menu.cliente);
            } else {
                System.err.println("Error: Menu.cliente es null al ir al Tablero.");
            }

            nuevaScene.getStylesheets().add(getClass().getResource("/Tablero/TableroStyles.css").toExternalForm());
            Stage stage = (Stage) rootPane.getScene().getWindow();
            stage.setScene(nuevaScene);
            stage.show();
        } catch (IOException e) {
            System.err.println("Error al cargar el Tablero.fxml: " + e.getMessage());
            e.printStackTrace();
        }
    }
}