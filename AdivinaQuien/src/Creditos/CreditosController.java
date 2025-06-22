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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.media.AudioClip;
import javafx.stage.Stage;

import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

// Clase controlador de la pantalla de créditos

public class CreditosController implements Initializable {

    // Paneles
    @FXML private Pane rootPane;
    @FXML private GridPane contentPane;
    @FXML private StackPane panel1;
    @FXML private StackPane panel2;
    @FXML private StackPane panel3;
    @FXML private StackPane panel4;

    // ImageViews
    @FXML private ImageView fondoImage;

    // Botones
    @FXML private Button buttonSalir;
    @FXML private Button buttonPortada;

    // Sonidos
    private static AudioClip sonidoPasto;
    private static AudioClip sonidoMeow;
    private static AudioClip sonidoClown;
    private static AudioClip sonidoZombie;
    private static AudioClip sonidoBonk;

    // Metodo que se ejecuta al cargar la escena
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // Instanciar los audios que utilizará la Stage
        sonidoPasto = new AudioClip(getClass().getResource("/Creditos/Assets/grass.mp3").toString());
        sonidoMeow = new AudioClip(getClass().getResource("/Creditos/Assets/meow.mp3").toString());
        sonidoClown = new AudioClip(getClass().getResource("/Creditos/Assets/clown.mp3").toString());
        sonidoZombie = new AudioClip(getClass().getResource("/Creditos/Assets/zombie.mp3").toString());
        sonidoBonk = new AudioClip(getClass().getResource("/Creditos/Assets/bonk.mp3").toString());

        // Adaptar la escena a la resolución del dispositivo
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

        // Adaptar el botón de salir a la resolución del dispositivo
        buttonSalir.prefWidthProperty().bind(rootPane.widthProperty().divide(10));
        buttonSalir.prefHeightProperty().bind(rootPane.heightProperty().divide(10));

        // Adaptar el botón  a la resolución del dispositivo
        buttonPortada.prefWidthProperty().bind(rootPane.widthProperty().divide(10));
        buttonPortada.prefHeightProperty().bind(rootPane.heightProperty().divide(10));

        // Adaptar los paneles a la resolución del dispositivo
        panel1.prefWidthProperty().bind(contentPane.widthProperty().divide(2));
        panel2.prefWidthProperty().bind(contentPane.widthProperty().divide(2));
        panel3.prefWidthProperty().bind(contentPane.widthProperty().divide(2));
        panel4.prefWidthProperty().bind(contentPane.widthProperty().divide(2));

        // Cargar el ícono del botón de salir
        Image imagenSalir = new Image(getClass().getResourceAsStream("/Creditos/Assets/salir.png"));
        ImageView imageView = new ImageView(imagenSalir);
        imageView.setFitWidth(45);
        imageView.setFitHeight(45);
        buttonSalir.setGraphic(imageView);

        // Cargar el ícono del botón para ir a la portada
        Image imagenPortada = new Image(getClass().getResourceAsStream("/Creditos/Assets/portadaIcon.png"));
        ImageView imageView2 = new ImageView(imagenPortada);
        imageView2.setFitWidth(45);
        imageView2.setFitHeight(45);
        buttonPortada.setGraphic(imageView2);
    }

    // Metodo para el botón que regresa al menú del juego
    public void bottonSalir(ActionEvent e){
        // Cargar el archivo FXML de la pantalla del menú y mostrarlo
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

    // Metodo para el botón que va a la portada
    public void buttonVerPortada(ActionEvent e) throws IOException {
        // Cargar el archivo FXML de la portada y la muestra
        Parent root = (Parent) FXMLLoader.load(getClass().getResource("/Portada/Portada.fxml"));
        Scene scene = rootPane.getScene();
        scene.getStylesheets().add(getClass().getResource("/Portada/PortadaStyles.css").toExternalForm());
        Stage stage = (Stage)((Node) e.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.hide();
        stage.setScene(scene);
        stage.show();
    }

    // Cargar los sonidos ambientales
    public void sonidoPasto(){
        sonidoPasto.setVolume(0.2);
        sonidoPasto.play();
    }

    public void sonidoMeow(){
        sonidoMeow.setVolume(0.2);
        sonidoMeow.play();
    }

    public void sonidoClown(){
        sonidoClown.setVolume(0.2);
        sonidoClown.play();
    }

    public void sonidoZombie(){
        sonidoZombie.setVolume(0.2);
        sonidoZombie.play();
    }

    public void sonidoBonk(){
        sonidoBonk.setVolume(0.2);
        sonidoBonk.play();
    }
}