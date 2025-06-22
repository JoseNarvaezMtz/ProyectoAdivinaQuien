package TerminarPartida;

import Menu.Menu;
import Menu.MenuController;
import Tablero.TableroController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
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

public class TerminarPartidaController extends MenuController implements Initializable {

    @FXML Pane rootPane;

    @FXML ImageView fondoImage;
    @FXML GridPane gridPane;
    @FXML GridPane gridPane2;

    @FXML Label tituloLabel;

    @FXML Button buttonRegresarMenu;
    @FXML Button buttonVolverAJugar;

    public static Boolean estado = false;

    //Musica
    public MediaPlayer musica;
    Media musicWin = new Media(getClass().getResource("/TerminarPartida/Assets/sunny.mp3").toString());
    Media musicLost = new Media(getClass().getResource("/TerminarPartida/Assets/rain.mp3").toString());
    AudioClip sonidoClick = new AudioClip(getClass().getResource("/TerminarPartida/Assets/confirmTab.mp3").toString());


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        if (TableroController.musica != null) {
            TableroController.musica.stop();
        }

        if (MenuController.musica != null){
            MenuController.musica.stop();
        }

        if (estado == true) {
            musica = new MediaPlayer(musicWin);
            musica.setCycleCount(MediaPlayer.INDEFINITE);
            musica.play();
        }
        else {
            musica = new MediaPlayer(musicLost);
            musica.setCycleCount(MediaPlayer.INDEFINITE);
            musica.play();
        }

        javafx.application.Platform.runLater(() -> {
            Stage stage = (Stage) rootPane.getScene().getWindow();
            stage.setFullScreen(Menu.fullScreen);
        });

        // Ajuste general de tamaños de los contenedores
        fondoImage.fitWidthProperty().bind(rootPane.widthProperty());
        fondoImage.fitHeightProperty().bind(rootPane.heightProperty());

        gridPane2.prefWidthProperty().bind(rootPane.widthProperty().divide(1.5));
        gridPane2.prefHeightProperty().bind(rootPane.heightProperty().divide(2));


        //Dependiendo de si ganó o perdió modifica los botones
        if (estado == true) {
            buttonVolverAJugar.getStyleClass().add("buttonPartidaFinWin");
            buttonRegresarMenu.getStyleClass().add("buttonPartidaFinWin");
            tituloLabel.getStyleClass().add("tituloPartidaFinWin");
        }
        else{
            tituloLabel.getStyleClass().add("tituloPartidaFinLose");
            buttonVolverAJugar.getStyleClass().add("buttonPartidaFinLose");
            buttonRegresarMenu.getStyleClass().add("buttonPartidaFinLose");
        }

        tituloLabel.prefWidthProperty().bind(rootPane.widthProperty().divide(1.5));
        tituloLabel.prefHeightProperty().bind(rootPane.heightProperty().divide(4));
        tituloLabel.setFont(new javafx.scene.text.Font(80));
        tituloLabel.setWrapText(true);
        tituloLabel.setMaxWidth(Double.MAX_VALUE);

        // Ajustar el tamaño de los botones con grid pane
        buttonVolverAJugar.prefWidthProperty().bind(rootPane.widthProperty().divide(3));
        buttonVolverAJugar.prefHeightProperty().bind(rootPane.heightProperty().divide(7));

        buttonRegresarMenu.prefWidthProperty().bind(rootPane.widthProperty().divide(3));
        buttonRegresarMenu.prefHeightProperty().bind(rootPane.heightProperty().divide(7));


        // Ajuste de gridPane
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(100);
        col1.setHgrow(Priority.ALWAYS);

        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(100);
        col2.setHgrow(Priority.ALWAYS);

        gridPane2.getColumnConstraints().addAll(col1, col2);

        GridPane.setHgrow(buttonVolverAJugar, Priority.ALWAYS);
        GridPane.setVgrow(buttonVolverAJugar, Priority.ALWAYS);

        GridPane.setHgrow(buttonRegresarMenu, Priority.ALWAYS);
        GridPane.setVgrow(buttonRegresarMenu, Priority.ALWAYS);

        Image imagenVolverAJugar = new Image(getClass().getResourceAsStream("/TerminarPartida/Assets/jugar.png"));
        ImageView imageView = new ImageView(imagenVolverAJugar);
        imageView.setFitWidth(60);
        imageView.setFitHeight(60);
        buttonVolverAJugar.setGraphic(imageView);
        buttonVolverAJugar.setMaxWidth(Double.MAX_VALUE);

        Image imagenRegresarAMenu = new Image(getClass().getResourceAsStream("/TerminarPartida/Assets/tabla.png"));
        ImageView imageView2 = new ImageView(imagenRegresarAMenu);
        imageView2.setFitWidth(60);
        imageView2.setFitHeight(60);
        buttonRegresarMenu.setGraphic(imageView2);
        buttonRegresarMenu.setMaxWidth(Double.MAX_VALUE);

        EstadoPartidaTerminada();
    }

    //Toma al padre para mandar a llamar al método del padre que manda al usuario a partidas registradas
    @Override
    public void partidasRegistradas(ActionEvent e) throws IOException {
        if(MenuController.desicionUsuario == true ){
            MenuController.musica.play();
        }
        musica.stop();
        super.partidasRegistradas(e);
    }

    //Toma al padre para mandar a llamar al método del padre que manda al usuario a Sala de espera
    @Override
    public void cambiarSalaDeEspera(ActionEvent e) throws IOException {
        if(MenuController.desicionUsuario == true ){
            MenuController.musica.play();
        }
        //Aqui agregar Sonido del click
        sonidoClick();

        // Se quiere jugar de nuevo
        if (Menu.cliente != null && !Menu.cliente.isClosed()){
            Menu.cliente.enviarMensajesC("JUGAR_OTRA_VEZ");
        }

        if (musica != null) {
            musica.stop();
        }

        super.cambiarSalaDeEspera(e);
    }

    public void EstadoPartidaTerminada() {
        if (estado == true) {
            fondoImage.setImage(new javafx.scene.image.Image(getClass().getResourceAsStream("/TerminarPartida/Assets/winner.png")));
            tituloLabel.setText("Felicidades, Ganaste!");

        } else {
            fondoImage.setImage(new javafx.scene.image.Image(getClass().getResourceAsStream("/TerminarPartida/Assets/loser.png")));
            tituloLabel.setText("Perdiste, Lo siento!");
        }
    }

    public void sonidoClick(){
        sonidoClick.setVolume(0.2);
        sonidoClick.play();
    }
}
