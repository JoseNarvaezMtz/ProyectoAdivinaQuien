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
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import Menu.MenuController;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import Sockets.Cliente.ClienteListener;
import Sockets.Cliente.MensajeListener;
import static Menu.MenuController.desicionUsuario;

// Clase controlador de la pantalla de la sala de espera

public class SalaDeEsperaController implements Initializable, ClienteListener, MensajeListener  {

    // Paneles
    @FXML private Pane rootPane;
    @FXML private GridPane contentPane;

    // ImageViews
    @FXML private ImageView fondoImage;
    @FXML private ImageView textureImg;
    @FXML private ImageView imageCargando;

    // Botones
    @FXML private Button buttonSalir;

    // labels
    @FXML private Label labelEsperando;

    private static MediaPlayer oceano; // Reproductor de música

    private Cliente cliente;

    // Metodo que se ejecuta al cargar la escena
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configurarUI(); // Primero configuramos la pantalla
        reproducirMusica(); // Despues reproducimos la musica
        gestionarConexion(); // Hacemos la conexion del cliente o la gestionamos en caso de que ya exista
    }

    // Metodo para configurar toda la interfáz de usuario
    private void configurarUI() {
        // Establecemos si la aplicación esta en pantalla completa o en ventana
        javafx.application.Platform.runLater(() -> {
            Stage stage = (Stage) rootPane.getScene().getWindow();
            stage.setFullScreen(Menu.fullScreen);
        });

        // Le damos un margen igual a la altura de la resolución entre 19 al label de esperando
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
        imageCargando.fitWidthProperty().bind(rootPane.widthProperty().divide(3));
        imageCargando.fitHeightProperty().bind(rootPane.heightProperty().divide(3));

        // Creamos una variable auxiliar que sea un número aleatorio entre 1 y 4
        Random random = new Random();
        int rand = random.nextInt(4)+1;

        // Se carga el gif correspondiente al random anterior y se le asigna a la pantalla
        Image img = new Image(getClass().getResourceAsStream("/SalaDeEspera/Assets/esperando" + rand + ".gif"));
        imageCargando.setImage(img);
        imageCargando.getStyleClass().add("imageGif"); // Se le asigna una clase para su estilo

        // Creación y carga del ícono para el botón de salir
        Image imagenSalir = new Image(getClass().getResourceAsStream("/SalaDeEspera/Assets/salir.png"));
        ImageView imageView = new ImageView(imagenSalir);
        imageView.setFitWidth(45);
        imageView.setFitHeight(45);
        buttonSalir.setGraphic(imageView);
    }

    // Metodo que reproduce la música
    private void reproducirMusica() {
        MenuController.musica.pause(); // Pausa la música entrante del menú

        // Carga la música para la ambientación de fondo
        Media fondo = new Media(getClass().getResource("/SalaDeEspera/Assets/ocean.mp3").toString());

        oceano = new MediaPlayer(fondo); // Crea un reproductor de música, asignándole la ambientación a reproducir
        oceano.setCycleCount(MediaPlayer.INDEFINITE);
        oceano.setVolume(0.4);
        oceano.play(); // Reproduce la música

        if (!desicionUsuario) { // Si el usuario silenció la música, entonces la muteamos
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
            // Cerramos la conexion del cliente, si existe
            if (Menu.cliente != null) {
                System.out.println("Desconectando al cliente");
                Menu.cliente.desconexion();
                Menu.cliente = null;
            }

            // Cargamos el archivo FXML de la pantalla del menú y la mostramos
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

    // Metodo que carga el tablero cuando se encuentra un oponente
    public void irTablero(String oponenteNick, List<Personaje> personajes) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Tablero/Tablero.fxml"));
            Parent nuevoRoot = loader.load();

            // 1. Obtener el controlador de la nueva pantalla
            TableroController tableroController = loader.getController();

            // 2. Pasar la conexión del cliente para que el tablero pueda enviar mensajes
            if (Menu.cliente != null) {
                tableroController.setCliente(Menu.cliente);
            } else {
                System.err.println("Error Crítico: El cliente es nulo al intentar ir al tablero.");
                return; // Salir si no hay cliente
            }

            // 3. ¡LA MAGIA! Llama al método que ya tienes en TableroController
            // para configurar toda la partida con los datos que acabas de recibir.
            tableroController.onIniciarPartida(oponenteNick, personajes);

            // 4. Finalmente, cambia la escena
            Scene nuevaScene = new Scene(nuevoRoot);
            nuevaScene.getStylesheets().add(getClass().getResource("/Tablero/TableroStyles.css").toExternalForm());
            Stage stage = (Stage) rootPane.getScene().getWindow();

            if (oceano != null) {
                oceano.stop();
            }

            stage.setScene(nuevaScene);
            stage.show();

        } catch (IOException e) {
            System.err.println("Error al cargar Tablero.fxml: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
        this.cliente.setClienteListener(this);
        this.cliente.setMensajeListener(this);
    }

    /** Cuando el servidor empareja y envía el paquete de inicio: */
    @Override
    public void onIniciarPartida(String oponenteNick, List<Personaje> personajes) {
        Platform.runLater(() -> {
            try {
                // 1) Carga la pantalla de Tablero
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Tablero/Tablero.fxml"));
                Parent root = loader.load();

                // 2) Inyecta el mismo Cliente y registra listeners en el TableroController
                TableroController tableroCtrl = loader.getController();
                tableroCtrl.setCliente(this.cliente);

                // 3) Le pasa los datos iniciales (mismo método que se usaba en ClienteListener)
                tableroCtrl.onIniciarPartida(oponenteNick, personajes);

                // 4) Cambia la escena
                Stage stage = (Stage) labelEsperando.getScene().getWindow();
                stage.setScene(new Scene(root));

            } catch (IOException ex) {
                ex.printStackTrace();
                labelEsperando.setText("Error al cargar el tablero.");
            }
        });
    }

    /** Aquí puedes mostrar cualquier otro mensaje de estado que venga por texto: */
    @Override
    public void onManejarMensajeServidor(String mensaje) {
        Platform.runLater(() -> {
            // por ejemplo, actualizar un log
            labelEsperando.setText("Servidor: " + mensaje);
        });
    }
}