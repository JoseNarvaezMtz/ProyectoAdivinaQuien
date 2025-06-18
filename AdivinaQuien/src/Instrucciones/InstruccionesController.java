//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package Instrucciones;

import Classes.Personaje;
import DataBaseClasses.PersonajeDB;
import Menu.Menu;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.media.AudioClip;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class InstruccionesController implements Initializable {
    @FXML private Pane rootPane;
    @FXML private GridPane contentPane;
    @FXML private GridPane contentPanePersonajes;

    @FXML private ImageView fondoImage;
    @FXML private ImageView personajeImage;

    @FXML private Button buttonSalir;
    @FXML private Button buttonLista;
    @FXML private Button buttonIzquierda;
    @FXML private Button buttonDerecha;
    @FXML private Button buttonRegresar;

    @FXML private Label Titulo;
    @FXML private TextArea instrucciones;

    @FXML private Label labelNombrePer;
    @FXML private TextArea textAreaDescripcion;

    // Array de imagenes para los personajes
    private List<Personaje> personajes = new ArrayList();
    // Contador que indica el indice actual de la imagen seleccionada
    private int indiceActual = 0;

    //Sonidos
    private static AudioClip sonidoSplash;
    private static AudioClip sonidoGota;

    public void initialize(URL url, ResourceBundle resourceBundle) {

        sonidoSplash = new AudioClip(getClass().getResource("/Instrucciones/Assets/splash.mp3").toString());
        sonidoGota = new AudioClip(getClass().getResource("/Instrucciones/Assets/gota.mp3").toString());

        Platform.runLater(() -> {
            Stage stage = (Stage)this.rootPane.getScene().getWindow();
            if (stage != null) {
                stage.setFullScreen(Menu.fullScreen);
            }
        });

        // Adapta la imagen de fondo a la resolución del dispositivo
        this.fondoImage.fitWidthProperty().bind(this.rootPane.widthProperty());
        this.fondoImage.fitHeightProperty().bind(this.rootPane.heightProperty());

        // Adapta el panel principal a la resolución del dispositivo mediante un margin
        this.contentPane.prefWidthProperty().bind(this.rootPane.widthProperty());
        this.contentPane.prefHeightProperty().bind(this.rootPane.heightProperty());

        // Permite mostrar o no los diferentes paneles mediante setVisible
        this.contentPanePersonajes.prefWidthProperty().bind(this.rootPane.widthProperty());
        this.contentPanePersonajes.prefHeightProperty().bind(this.rootPane.heightProperty());

        // Adapta el titulo del panel a la resolución del dispositivo mediante un margin
        this.Titulo.prefWidthProperty().bind(this.rootPane.widthProperty().multiply(0.85));
        this.Titulo.prefHeightProperty().bind(this.rootPane.heightProperty().multiply(0.1));

        // Adapta un TextArea que muestra las instrucciones del juego y la adapta a la resolución del dispositivo
        this.instrucciones.prefWidthProperty().bind(this.rootPane.widthProperty().multiply(0.85));
        this.instrucciones.prefHeightProperty().bind(this.rootPane.heightProperty().multiply(0.6));

        // Adapta el botón de regreso a la resolución del dispositivo
        this.buttonSalir.prefWidthProperty().bind(this.rootPane.widthProperty().divide(12));
        this.buttonSalir.prefHeightProperty().bind(this.rootPane.heightProperty().divide(10));

        this.buttonRegresar.prefWidthProperty().bind(this.rootPane.widthProperty().divide(12));
        this.buttonRegresar.prefHeightProperty().bind(this.rootPane.heightProperty().divide(10));

        // Adapta el botón que muestra la lista de los personajes a la resolución del dispositivo
        this.buttonLista.prefWidthProperty().bind(this.rootPane.widthProperty().divide(4));
        this.buttonLista.prefHeightProperty().bind(this.rootPane.heightProperty().divide(8));

        // Adapta el root principal a la resolución del dispositivo
        this.rootPane.widthProperty().addListener((obs, oldVal, newVal) -> this.ajustarFuentes());
        this.rootPane.heightProperty().addListener((obs, oldVal, newVal) -> this.ajustarFuentes());

        // Adapta la imagen de los personajes a la resolución del dispositivo
        this.personajeImage.fitWidthProperty().bind(this.rootPane.widthProperty().divide(4));
        this.personajeImage.fitHeightProperty().bind(this.rootPane.heightProperty().divide(2));

        //Ajusta las fuentes que se muestran en el panel principal para que se adapte a la resolución general del dispositivo
        this.ajustarFuentes();

        // Carga la imagen del botón de salir
        Image imagenSalir = new Image(this.getClass().getResourceAsStream("/Instrucciones/Assets/regresar.png"));
        ImageView imageView = new ImageView(imagenSalir);
        imageView.setFitWidth((double)50.0F);
        imageView.setFitHeight((double)50.0F);
        this.buttonSalir.setGraphic(imageView);

        Image imagenRegresar = new Image(this.getClass().getResourceAsStream("/Instrucciones/Assets/regresar.png"));
        ImageView imgV = new ImageView(imagenRegresar);
        imgV.setFitWidth((double)50.0F);
        imgV.setFitHeight((double)50.0F);
        this.buttonRegresar.setGraphic(imgV);

        // Carga la imagen del botón para cambiar al personaje anterior de la lista
        Image imagenIzquierda = new Image(this.getClass().getResourceAsStream("/Instrucciones/Assets/flechaIzquierda.png"));
        ImageView imageView2 = new ImageView(imagenIzquierda);
        imageView2.setFitWidth((double)50.0F);
        imageView2.setFitHeight((double)50.0F);
        this.buttonIzquierda.setGraphic(imageView2);

        // Carga la imagen del botón para cambiar al personaje siguiente de la lista
        Image imagenDerecha = new Image(this.getClass().getResourceAsStream("/Instrucciones/Assets/flechaDerecha.png"));
        ImageView imageView3 = new ImageView(imagenDerecha);
        imageView3.setFitWidth((double)50.0F);
        imageView3.setFitHeight((double)50.0F);
        this.buttonDerecha.setGraphic(imageView3);
    }

    // Botón para regresar al menú principal
    @FXML
    public void salir(ActionEvent e) {
        try {
            Parent menuRoot = (Parent)FXMLLoader.load(this.getClass().getResource("/Menu/Menu.fxml"));
            Scene scene = new Scene(menuRoot);
            scene.getStylesheets().add(this.getClass().getResource("/Menu/MenuStyles.css").toExternalForm());
            Stage stage = (Stage)((Node)e.getSource()).getScene().getWindow();
            stage.hide();
            stage.setScene(scene);
            stage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    // Botón para mostrar la lista de los personajes, donde pone en invisible el panel principal
    // y muestra el panel con las imagenes de los personajes
    @FXML
    public void menuPersonajesInstrucciones(ActionEvent e) {
        this.contentPane.setVisible(false);
        this.contentPanePersonajes.setVisible(true);
        this.cargarPersonajes();
        this.actualizarPersonaje();
    }

    // Botón para cambiar al personaje anterior de la lista
    @FXML
    public void izquierda(ActionEvent e) {
        if (this.indiceActual > 0) {
            this.indiceActual--;
        } else {
            this.indiceActual = this.personajes.size()-1;
        }

        this.actualizarPersonaje();
    }

    // Botón para cambiar al personaje siguiente de la lista
    @FXML
    public void derecha(ActionEvent e) {
        if (this.indiceActual < this.personajes.size()-1) {
            this.indiceActual++;
        } else {
            this.indiceActual = 0;
        }

        this.actualizarPersonaje();
    }

    // Ajusta las fuentes de los paneles para que con una operación se muestren conforme a la
    // resolución del dispositivo.
    private void ajustarFuentes() {
        double ancho = this.rootPane.getWidth();
        double alto = this.rootPane.getHeight();
        double escala = Math.min(ancho / (double)1280.0F, alto / (double)720.0F);
        this.instrucciones.setFont(new Font("Arial", (double)24.0F * escala));
        this.buttonLista.setFont(new Font("Arial", (double)17.0F * escala));
        this.Titulo.setFont(new Font("Chiller", (double)48.0F * escala));
        this.Titulo.setAlignment(Pos.CENTER);
    }

    // Carga las imagenes para que se puedan mostrar en el panel con las imagenes de los personajes
    private void cargarPersonajes() {
        this.personajes = PersonajeDB.getPersonajes();
    }

    // Cambia la imagen actual que tiene el panel
    private void actualizarPersonaje() {
        this.personajeImage.setImage(personajes.get(indiceActual).getImagenFX());
        this.labelNombrePer.setText(this.personajes.get(this.indiceActual).getNombre());
        this.textAreaDescripcion.setText(this.personajes.get(this.indiceActual).getDescripcionString());
    }

    //Regresa al menú que muestra las instrucciones
    public void regresarInstrucciones(ActionEvent e) {
        this.contentPanePersonajes.setVisible(false);
        this.contentPane.setVisible(true);
    }

    //Sonidos
    public void sonidoSplash(){
        sonidoSplash.setVolume(0.4);
        sonidoSplash.play();
    }

    public void sonidoGota(){
        sonidoGota.setVolume(0.2);
        sonidoGota.play();
    }
}
