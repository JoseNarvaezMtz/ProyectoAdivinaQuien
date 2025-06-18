package Portada;

import Creditos.CreditosController;
import Menu.Menu;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import Menu.MenuController;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static Menu.MenuController.desicionUsuario;

public class PortadaController implements Initializable{

    @FXML private Pane rootPane;

    @FXML private GridPane gridPane;

    @FXML private ImageView imageLogoUaa;
    @FXML private ImageView imageLogoCentro;
    @FXML private ImageView imageChanguito1;
    @FXML private ImageView imageChanguito2;

    @FXML private Button buttonSalir;

    @FXML private TextFlow textFlow;

    private Stage stage;
    private Scene scene;
    private Parent root;

    public static MediaPlayer musica;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        if (musica == null) {
            Media music = new Media(getClass().getResource("/Portada/Assets/CircusMusic.mp3").toString());
            musica = new MediaPlayer(music);
            musica.setCycleCount(MediaPlayer.INDEFINITE);
        }

        javafx.application.Platform.runLater(() -> {
            if (rootPane.getScene() != null) {
                Stage stage = (Stage) rootPane.getScene().getWindow();
                stage.setFullScreen(Menu.fullScreen);
            }
        });

        // Ajustar GridPane al tamaño del rootPane
        gridPane.prefWidthProperty().bind(rootPane.widthProperty());
        gridPane.prefHeightProperty().bind(rootPane.heightProperty());

        // Ajustar TextFlow (ocupará toda la fila central)
        textFlow.prefWidthProperty().bind(rootPane.widthProperty().multiply(0.615));
        textFlow.prefHeightProperty().bind(rootPane.heightProperty().multiply(0.4));

        // Ajustar imagen UAA (izquierda arriba)
        imageLogoUaa.fitWidthProperty().bind(rootPane.widthProperty().divide(6));
        imageLogoUaa.fitHeightProperty().bind(rootPane.heightProperty().divide(4));

        // Ajustar imagen Centro (derecha arriba)
        imageLogoCentro.fitWidthProperty().bind(rootPane.widthProperty().divide(6));
        imageLogoCentro.fitHeightProperty().bind(rootPane.heightProperty().divide(4));

        // Ajustamos a los changuitos
        imageChanguito1.fitWidthProperty().bind(rootPane.widthProperty().divide(5));
        imageChanguito1.fitHeightProperty().bind(rootPane.heightProperty().divide(3));

        imageChanguito2.fitWidthProperty().bind(rootPane.widthProperty().divide(5));
        imageChanguito2.fitHeightProperty().bind(rootPane.heightProperty().divide(3));

        // Ajustar botón salir (abajo)
        Image iconoSalir = new Image(getClass().getResourceAsStream("/Portada/Assets/salir.png"));
        ImageView iconoView = new ImageView(iconoSalir);
        iconoView.setFitWidth(45);
        iconoView.setFitHeight(45);
        buttonSalir.setGraphic(iconoView);

        buttonSalir.prefWidthProperty().bind(rootPane.widthProperty().divide(12));
        buttonSalir.prefHeightProperty().bind(rootPane.heightProperty().divide(14));

        // Configurar contenido del TextFlow
        configurarTextFlow();
    }

    public void configurarTextFlow() {
        String[][] contenido = {
                {"Centro: ", "\nCentro de Ciencias Básicas"},
                {"Departamento: ", "\nElectrónica"},
                {"Carrera: ", "\nIngeniería en Sistemas Computacionales"},
                {"Materia: ", "\nProgramación III"},
                {"Actividad: ", "\nCaracterísticas del Lenguaje Java"},
                {"Profesor: ", "\n Dra. Georgina Salazar Partida"},
                {"Nombre del estudiante: ", "\nHarim Jesús Enrique Dueñas Dávila\nJulián Emmanuel Hernández\nJosé Luis Narváez Martínez\nEmiliano Alejandro Santos González\n"},
                {"Semestre: ", "\n4º Grupo A"},
                {"Horario: ", "\n09:00 – 10:00 horas"},
                {"Unidad: ", "\nVII"},
                {"Fecha de entrega: ", "\n22 de Junio de 2025"}
        };

        for (String[] par : contenido) {
            Text titulo = new Text(par[0]);
            titulo.setFont(Font.font("Arial Black", 24));

            Text valor = new Text(par[1] + "\n\n");
            valor.setFont(Font.font("Arial", 18));

            this.textFlow.getChildren().addAll(titulo, valor);
        }
    }

    // BOTÓN PARA IR A LA PANTALLA DE CRÉDITOS
    public void cambiarCreditos(ActionEvent e) throws IOException {
        musica.stop();
        Parent root = (Parent) FXMLLoader.load(getClass().getResource("/Creditos/Creditos.fxml"));
        Scene scene = rootPane.getScene();
        scene.getStylesheets().add(getClass().getResource("/Creditos/CreditosStyles.css").toExternalForm());
        stage = (Stage)((Node) e.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.hide();
        stage.setScene(scene);
        stage.show();
    }

    public void EasterEggMusic(){
        MenuController.musica.pause();
        imageChanguito1.setOpacity(1);
        imageChanguito2.setOpacity(1);
        //Reproducimos la música
        musica.setVolume(0.2);
        musica.play();
        //vemos si el usuario quiere escuchar música
        //Si decide que no, pone la música en muted
        if (!desicionUsuario) {
            musica.setMute(true);
        } else {
            musica.setMute(false);
        }
    }

}
