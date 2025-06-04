package Tablero;

import Menu.Menu;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.net.URL;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class TableroController implements Initializable {
    @FXML Pane rootPane;
    @FXML Label labelJugador;
    @FXML ImageView fondoImage;
    @FXML GridPane contentPane;
    @FXML TextArea chat;
    @FXML TextField textFieldMensaje;
    @FXML Label tiempoPartida;

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

        labelJugador.setText(Menu.nickName);

        reloj();

    }

    public void enviarMensaje(ActionEvent e){
        String mensaje = Menu.nickName + ": " + textFieldMensaje.getText() + "\n";
        chat.appendText(mensaje);
        textFieldMensaje.clear();
    }

    private long segundosTranscurridos = 0;
    public void reloj() {
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(1), event -> {
                    segundosTranscurridos++;

                    long horas = segundosTranscurridos / 3600;
                    long minutos = (segundosTranscurridos % 3600) / 60;
                    long segundos = segundosTranscurridos % 60;

                    String tiempoFormateado = String.format("%02d:%02d:%02d", horas, minutos, segundos);
                    tiempoPartida.setText(tiempoFormateado);
                })
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

}
