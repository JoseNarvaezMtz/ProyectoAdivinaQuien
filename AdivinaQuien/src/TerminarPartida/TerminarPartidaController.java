package TerminarPartida;

import Menu.Menu;
import Menu.MenuController;
import Tablero.TableroController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

// Clase controladora de la pantalla de ganar y perder

public class TerminarPartidaController extends MenuController implements Initializable {

    // Paneles
    @FXML Pane rootPane;
    @FXML GridPane gridPane;
    @FXML GridPane gridPane2;

    // ImageViews
    @FXML ImageView fondoImage;

    // Labels
    @FXML Label tituloLabel;


    // Botones
    @FXML Button buttonRegresarMenu;
    @FXML Button buttonVolverAJugar;

    // Booleano auxiliar que indica si el usuario perdió o ganó
    public static Boolean estado = false;

    public MediaPlayer musica; // Reproductor de música

    // Música ambiental
    Media musicWin = new Media(getClass().getResource("/TerminarPartida/Assets/sunny.mp3").toString());
    Media musicLost = new Media(getClass().getResource("/TerminarPartida/Assets/rain.mp3").toString());
    AudioClip sonidoClick = new AudioClip(getClass().getResource("/TerminarPartida/Assets/confirmTab.mp3").toString());

    // Metodo que se ejecuta al cargar la escena
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (TableroController.musica != null) { // Si existe música reproduciéndose desde el tablero
            TableroController.musica.stop(); // La detenemos
        }

        if (MenuController.musica != null){ // Si existe música reproduciéndose desde el menú
            MenuController.musica.stop(); // La detenemos
        }

        if (estado == true) {  // En caso de que el usuario haya ganado
            // Se reproduce la música de victoria
            musica = new MediaPlayer(musicWin);
            musica.setCycleCount(MediaPlayer.INDEFINITE);
            musica.setVolume(0.2);
            musica.play();
        } else { // En caso de que el usuario haya perdido
            // Se reproduce la música de derrota
            musica = new MediaPlayer(musicLost);
            musica.setCycleCount(MediaPlayer.INDEFINITE);
            musica.setVolume(0.2);
            musica.play();
        }

        // Se adapta la aplicación a su modo pantalla completa o ventana dependiendo de la variable auxiliar "fullScreen"
        javafx.application.Platform.runLater(() -> {
            Stage stage = (Stage) rootPane.getScene().getWindow();
            stage.setFullScreen(Menu.fullScreen);
        });

        // Adaptar la imágen de fondo a la resolución del dispositivo
        fondoImage.fitWidthProperty().bind(rootPane.widthProperty());
        fondoImage.fitHeightProperty().bind(rootPane.heightProperty());

        // Adaptar el panel del contenido a la resolución del dispositivo
        gridPane2.prefWidthProperty().bind(rootPane.widthProperty().divide(1.5));
        gridPane2.prefHeightProperty().bind(rootPane.heightProperty().divide(2));

        // Adaptar el tamaño del label del título a la resolución del dispositivo
        tituloLabel.prefWidthProperty().bind(rootPane.widthProperty());
        tituloLabel.prefHeightProperty().bind(rootPane.heightProperty());

        // Crear y asignar fuentes al label del título, además de algunas configuraciones para que se vea estético
        tituloLabel.setFont(new javafx.scene.text.Font(80));
        tituloLabel.setWrapText(true);
        tituloLabel.setMaxWidth(Double.MAX_VALUE);

        // Adaptar el tamaño del botón de volver a jugar a la resolución del dispositivo
        buttonVolverAJugar.prefWidthProperty().bind(rootPane.widthProperty().divide(3));
        buttonVolverAJugar.prefHeightProperty().bind(rootPane.heightProperty().divide(7));

        // Adaptar el tamaño del botón de regresar al menú a la resolución del dispositivo
        buttonRegresarMenu.prefWidthProperty().bind(rootPane.widthProperty().divide(3));
        buttonRegresarMenu.prefHeightProperty().bind(rootPane.heightProperty().divide(7));

        // Se le asignan porcentajes a las columnas del gridPane principal
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(100);
        col1.setHgrow(Priority.ALWAYS);

        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(100);
        col2.setHgrow(Priority.ALWAYS);

        // Se le asignan los porcentajes de las columnas al gridPane
        gridPane2.getColumnConstraints().addAll(col1, col2);

        // Se asigna una forma de expandirse a los nodos que se encuentran dentro del gridPane al crecer
        GridPane.setHgrow(buttonVolverAJugar, Priority.ALWAYS);
        GridPane.setVgrow(buttonVolverAJugar, Priority.ALWAYS);

        GridPane.setHgrow(buttonRegresarMenu, Priority.ALWAYS);
        GridPane.setVgrow(buttonRegresarMenu, Priority.ALWAYS);

        // Cargar el ícono del botón de volver a jugar
        Image imagenVolverAJugar = new Image(getClass().getResourceAsStream("/TerminarPartida/Assets/jugar.png"));
        ImageView imageView = new ImageView(imagenVolverAJugar);
        imageView.setFitWidth(60);
        imageView.setFitHeight(60);
        buttonVolverAJugar.setGraphic(imageView);
        buttonVolverAJugar.setMaxWidth(Double.MAX_VALUE);

        // Cargar el ícono del botón de regresar al menú
        Image imagenRegresarAMenu = new Image(getClass().getResourceAsStream("/TerminarPartida/Assets/tabla.png"));
        ImageView imageView2 = new ImageView(imagenRegresarAMenu);
        imageView2.setFitWidth(60);
        imageView2.setFitHeight(60);
        buttonRegresarMenu.setGraphic(imageView2);
        buttonRegresarMenu.setMaxWidth(Double.MAX_VALUE);

        // Dependiendo de si ganó o perdió, se modifican los botones
        if (estado == true) { // Si ganó
            // Se le añaden clases de victoria para sus estilos
            buttonVolverAJugar.getStyleClass().add("buttonPartidaFinWin");
            buttonRegresarMenu.getStyleClass().add("buttonPartidaFinWin");
            tituloLabel.getStyleClass().add("tituloPartidaFinWin");
        } else{ // Si perdió
            // Se le añaden clases de derrota para sus estilos
            tituloLabel.getStyleClass().add("tituloPartidaFinLose");
            buttonVolverAJugar.getStyleClass().add("buttonPartidaFinLose");
            buttonRegresarMenu.getStyleClass().add("buttonPartidaFinLose");
        }

        EstadoPartidaTerminada(); // Metodo que determina qué fondo cargar según si ganó o perdió el usuario
    }

    // Toma al padre para mandar a llamar al metodo del padre que manda al usuario a partidas registradas
    @Override
    public void partidasRegistradas(ActionEvent e) throws IOException {
        if(MenuController.desicionUsuario == true ){ // Si el usuario no silenció la música, se reproduce
            MenuController.musica.play();
        }
        musica.stop();

        super.partidasRegistradas(e);
    }

    // Toma al padre para mandar a llamar al metodo del padre que manda al usuario a Sala de espera
    @Override
    public void cambiarSalaDeEspera(ActionEvent e) throws IOException {
        if(MenuController.desicionUsuario == true ){ // Si el usuario no silenció la música, se reproduce
            MenuController.musica.play();
        }
        // Se reproduce el sonido del click
        sonidoClick();

        // Se quiere jugar de nuevo
        if (Menu.cliente != null && !Menu.cliente.isClosed()){
            Menu.cliente.enviarMensajesC("JUGAR_OTRA_VEZ");
        }

        if (musica != null) { // Si existe música reproduciéndose
            musica.stop(); // Se detiene la música
        }

        super.cambiarSalaDeEspera(e); // Se llama al metodo para cambiar la pantalla a la sala de espera
    }

    // Metodo que carga la imágen de fondo según si el usuario ganó o perdió
    public void EstadoPartidaTerminada() {
        if (estado == true) { // Si ganó
            fondoImage.setImage(new javafx.scene.image.Image(getClass().getResourceAsStream("/TerminarPartida/Assets/winner.png")));
            tituloLabel.setText("Felicidades, Ganaste!"); // Se establece el mensaje de ganador

        } else { // Si perdió
            fondoImage.setImage(new javafx.scene.image.Image(getClass().getResourceAsStream("/TerminarPartida/Assets/loser.png")));
            tituloLabel.setText("Perdiste, Lo siento!"); // Se establece el mensaje de perdedor
        }
    }

    // Metodo que reproduce el sonido del click
    public void sonidoClick(){
        sonidoClick.setVolume(0.2);
        sonidoClick.play();
    }
}
