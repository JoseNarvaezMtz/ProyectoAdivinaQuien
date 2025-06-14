package TerminarPartida;

import Menu.Menu;
import Menu.MenuController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import javax.swing.plaf.RootPaneUI;
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
    // winner = true
    // loser = false
    public Boolean estado = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        javafx.application.Platform.runLater(() -> {
            Stage stage = (Stage) rootPane.getScene().getWindow();
            stage.setFullScreen(Menu.fullScreen);
        });

        gridPane2.prefWidthProperty().bind(rootPane.widthProperty());
        gridPane2.prefHeightProperty().bind(rootPane.heightProperty());

        // Ajustar el fondo a la resolución de pantalla
        fondoImage.fitWidthProperty().bind(rootPane.widthProperty());
        fondoImage.fitHeightProperty().bind(rootPane.heightProperty());

        // Ajustar el tamaño del título
        tituloLabel.prefWidthProperty().bind(rootPane.widthProperty().divide(1.5));
        tituloLabel.prefHeightProperty().bind(rootPane.heightProperty().divide(4));

        // Ajustar el tamaño de fuente
        tituloLabel.setFont(new javafx.scene.text.Font(80));

        // Ajustar los botones
        buttonVolverAJugar.prefWidthProperty().bind(rootPane.widthProperty().divide(9));
        buttonVolverAJugar.prefHeightProperty().bind(rootPane.heightProperty().divide(11));

        buttonRegresarMenu.prefWidthProperty().bind(rootPane.widthProperty().divide(9));
        buttonRegresarMenu.prefHeightProperty().bind(rootPane.heightProperty().divide(11));

        // CREACIÓN Y CARGA DEL ÍCONO PARA EL BOTÓN VOLVER A JUGAR
        Image imagenVolverAJugar = new Image(getClass().getResourceAsStream("/TerminarPartida/Assets/jugar.png"));
        ImageView imageView = new ImageView(imagenVolverAJugar);
        imageView.setFitWidth(45);
        imageView.setFitHeight(45);
        buttonVolverAJugar.setGraphic(imageView);

        // CREACIÓN Y CARGA DEL ÍCONO PARA EL BOTÓN REGRESAR A MENU
        Image imagenRegresarAMenu = new Image(getClass().getResourceAsStream("/TerminarPartida/Assets/tabla.png"));
        ImageView imageView2 = new ImageView(imagenRegresarAMenu);
        imageView2.setFitWidth(45);
        imageView2.setFitHeight(45);
        buttonRegresarMenu.setGraphic(imageView2);

        EstadoPartidaTerminada();
    }

    @Override
    public void partidasRegistradas(ActionEvent e) throws IOException {
        super.partidasRegistradas(e);
    }

    @Override
    public void cambiarSalaDeEspera(ActionEvent e) throws IOException {
        super.cambiarSalaDeEspera(e);
    }

    public void EstadoPartidaTerminada(){
        tituloLabel.setWrapText(true);
        tituloLabel.setMaxWidth(Double.MAX_VALUE);

        if(estado == true){
            fondoImage.setImage(new javafx.scene.image.Image(getClass().getResourceAsStream("/TerminarPartida/Assets/winner.png")));
            tituloLabel.setText("Felicidades, Ganaste!");

        }
        else{
            fondoImage.setImage(new javafx.scene.image.Image(getClass().getResourceAsStream("/TerminarPartida/Assets/loser.png")));
            tituloLabel.setText("Perdiste,    Lo siento!");
        }
    }
}
