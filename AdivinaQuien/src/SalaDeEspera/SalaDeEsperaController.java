package SalaDeEspera;

import java.util.List;
import java.util.Random;

import Classes.Personaje;
import Menu.Menu;
import Sockets.Cliente;
import Tablero.TableroController;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import Menu.MenuController;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static Menu.MenuController.desicionUsuario;

public class SalaDeEsperaController implements Initializable {

    @FXML Pane rootPane;
    @FXML GridPane contentPane;
    @FXML GridPane tablePane;

    @FXML ImageView fondoImage;
    @FXML ImageView textureImg;
    @FXML ImageView imageCargando;
    @FXML Group gifPane;

    @FXML Button buttonSalir;

    @FXML Label labelEsperando;

    //Musica
    private static MediaPlayer oceano;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configurarUI(); // Primero configuramos la pantalla
        reproducirMusica(); // Despues reproducimos la musica
        gestionarConexion(); // Hacemos la conexion del cliente o la gestionamos en caso de que ya exista
    }


    private void configurarUI() {
        // Metodo que se encarga de mantener la pantalla completa
        javafx.application.Platform.runLater(() -> {
            Stage stage = (Stage) rootPane.getScene().getWindow();
            stage.setFullScreen(Menu.fullScreen);
        });

        // Es un metodo de JavaFX que permite ejecutar código en el hilo principal de la interfaz de usuario
        Platform.runLater(() -> {
            double height = rootPane.getHeight();

            GridPane.setMargin(labelEsperando, new Insets(
                    height/19,
                    0,
                    0,
                    0
            ));
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

        Random random = new Random();
        int rand = random.nextInt(4)+1;
        Image img = new Image(getClass().getResourceAsStream("/SalaDeEspera/Assets/esperando" + rand + ".gif"));
        imageCargando.setImage(img);
        imageCargando.getStyleClass().add("imageGif");

        // CREACIÓN Y CARGA DEL ÍCONO PARA EL BOTÓN DE SALIR
        Image imagenSalir = new Image(getClass().getResourceAsStream("/SalaDeEspera/Assets/salir.png"));
        ImageView imageView = new ImageView(imagenSalir);
        imageView.setFitWidth(45);
        imageView.setFitHeight(45);
        buttonSalir.setGraphic(imageView);
    }

    private void reproducirMusica() {
        //Pausa la música del menú
        MenuController.musica.pause();

        //Sonido de Fondo
        Media fondo = new Media(getClass().getResource("/SalaDeEspera/Assets/ocean.mp3").toString());

        oceano = new MediaPlayer(fondo);
        oceano.setCycleCount(MediaPlayer.INDEFINITE);
        oceano.setVolume(0.4);
        oceano.play();

        //vemos si el usuario quiere escuchar música
        //Si decide que no, pone la música en muted
        if (!desicionUsuario) {
            oceano.setMute(true);
        } else {
            oceano.setMute(false);
        }
    }

    private void gestionarConexion() {
        // REVANCHA: (el cliente ya existe). Esto es rápido y no necesita hilo.
        if (Menu.cliente != null && !Menu.cliente.isClosed()) {
            System.out.println("SalaDeEspera: Conexión existente encontrada. Reutilizándola.");
            labelEsperando.setText("Esperando revancha...");
            Menu.cliente.setClienteListener((oponenteNick, personajes) -> {
                Platform.runLater(() -> irTablero(oponenteNick, personajes));
            });
            return;
        }

        // NUEVA PARTIDA: La conexión se crea en un hilo secundario para NO congelar la UI.
        labelEsperando.setText("Conectando al servidor...");
        new Thread(() -> {
            try {
                // Esta operación de red ahora es segura y no afecta la UI.
                Menu.cliente = new Cliente("localhost", 5000, (oponenteNick, personajes) -> {
                    Platform.runLater(() -> irTablero(oponenteNick, personajes));
                });
                // Actualizamos la UI desde el hilo secundario usando Platform.runLater
                Platform.runLater(() -> labelEsperando.setText("¡Conectado! Buscando oponente..."));
            } catch (IOException ex) {
                Platform.runLater(() -> labelEsperando.setText("Error de conexión. Vuelve al menú."));
                ex.printStackTrace();
            }
        }).start();
    }

    // Metodo para regresar al Menú principal
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
            oceano.stop();
            stage.hide();
            stage.setScene(scene);
            stage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    // Carga el tablero cuando se encuentra el oponente
    public void irTablero(String oponenteNick, List<Personaje> personajes) {
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
            //Pasamos el nick del oponente
            tableroController.setOponente(oponenteNick);

            tableroController.onPersonajesRecibidos(personajes);

            nuevaScene.getStylesheets().add(getClass().getResource("/Tablero/TableroStyles.css").toExternalForm());
            Stage stage = (Stage) rootPane.getScene().getWindow();
            oceano.stop();
            stage.setScene(nuevaScene);
            stage.show();
        } catch (IOException e) {
            System.err.println("Error al cargar el Tablero.fxml: " + e.getMessage());
            e.printStackTrace();
        }
    }
}