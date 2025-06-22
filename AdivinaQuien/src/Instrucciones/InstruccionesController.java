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
import javafx.stage.Stage;

// Clase controlador de la pantalla de isntrucciones

public class InstruccionesController implements Initializable {

    // Paneles
    @FXML private Pane rootPane;
    @FXML private GridPane contentPane;
    @FXML private GridPane contentPanePersonajes;

    // ImageViews
    @FXML private ImageView fondoImage;
    @FXML private ImageView personajeImage;

    // ImageViews de las instrucciones
    @FXML private ImageView img1;
    @FXML private ImageView img2;
    @FXML private ImageView img3;
    @FXML private ImageView img4;
    @FXML private ImageView img5;
    @FXML private ImageView img6;
    @FXML private ImageView img7;

    // TextAreas
    @FXML private TextArea textAreaDescripcion;

    // TextAreas de las instrucciones
    @FXML private TextArea textArea1;
    @FXML private TextArea textArea2;
    @FXML private TextArea textArea3;
    @FXML private TextArea textArea4;
    @FXML private TextArea textArea5;
    @FXML private TextArea textArea6;
    @FXML private TextArea textArea7;

    // Botones
    @FXML private Button buttonSalir;
    @FXML private Button buttonLista;
    @FXML private Button buttonIzquierda;
    @FXML private Button buttonDerecha;
    @FXML private Button buttonRegresar;

    // Labels
    @FXML private Label Titulo;
    @FXML private Label labelNombrePer;

    //Sonidos
    private static AudioClip sonidoSplash;
    private static AudioClip sonidoGota;

    // Array de personajes para la lista de estos
    private List<Personaje> personajes = new ArrayList();

    // Contador que indica el indice actual de la imagen seleccionada
    private int indiceActual = 0;

    // Metodo que se ejecuta al cargar la escena
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // Cargar los sonidos ambientales
        sonidoSplash = new AudioClip(getClass().getResource("/Instrucciones/Assets/splash.mp3").toString());
        sonidoGota = new AudioClip(getClass().getResource("/Instrucciones/Assets/gota.mp3").toString());

        // Adaptar la pantalla a su versión completa o ventana
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

        // Adapta los TextArea de las instrucciones a la resolución del dispositivo

        this.textArea1.prefWidthProperty().bind(this.rootPane.widthProperty().multiply(0.73));
        this.textArea1.prefHeightProperty().bind(this.rootPane.heightProperty().multiply(0.95));

        this.textArea2.prefWidthProperty().bind(this.rootPane.widthProperty().multiply(0.73));
        this.textArea2.prefHeightProperty().bind(this.rootPane.heightProperty().multiply(0.58));

        this.textArea3.prefWidthProperty().bind(this.rootPane.widthProperty().multiply(0.73));
        this.textArea3.prefHeightProperty().bind(this.rootPane.heightProperty().multiply(0.38));

        this.textArea4.prefWidthProperty().bind(this.rootPane.widthProperty().multiply(0.73));
        this.textArea4.prefHeightProperty().bind(this.rootPane.heightProperty().multiply(0.80));

        this.textArea5.prefWidthProperty().bind(this.rootPane.widthProperty().multiply(0.73));
        this.textArea5.prefHeightProperty().bind(this.rootPane.heightProperty().multiply(0.39));

        this.textArea6.prefWidthProperty().bind(this.rootPane.widthProperty().multiply(0.73));
        this.textArea6.prefHeightProperty().bind(this.rootPane.heightProperty().multiply(0.78));

        this.textArea7.prefWidthProperty().bind(this.rootPane.widthProperty().multiply(0.73));
        this.textArea7.prefHeightProperty().bind(this.rootPane.heightProperty().multiply(0.64));

        // Adapta las imágenes de las instrucciones a la resolución del dispositivo

        this.img1.fitWidthProperty().bind(this.rootPane.widthProperty().multiply(0.8));
        this.img1.fitHeightProperty().bind(this.rootPane.widthProperty().multiply(0.45));

        this.img2.fitWidthProperty().bind(this.rootPane.widthProperty().multiply(0.8));
        this.img2.fitHeightProperty().bind(this.rootPane.widthProperty().multiply(0.45));

        this.img3.fitWidthProperty().bind(this.rootPane.widthProperty().multiply(0.8));
        this.img3.fitHeightProperty().bind(this.rootPane.widthProperty().multiply(0.45));

        this.img4.fitWidthProperty().bind(this.rootPane.widthProperty().multiply(0.8));
        this.img4.fitHeightProperty().bind(this.rootPane.widthProperty().multiply(0.45));

        this.img5.fitWidthProperty().bind(this.rootPane.widthProperty().multiply(0.8));
        this.img5.fitHeightProperty().bind(this.rootPane.widthProperty().multiply(0.45));

        this.img6.fitWidthProperty().bind(this.rootPane.widthProperty().multiply(0.8));
        this.img6.fitHeightProperty().bind(this.rootPane.widthProperty().multiply(0.45));

        this.img7.fitWidthProperty().bind(this.rootPane.widthProperty().multiply(0.8));
        this.img7.fitHeightProperty().bind(this.rootPane.widthProperty().multiply(0.45));

        // Adapta el botón de regreso a la resolución del dispositivo
        this.buttonSalir.prefWidthProperty().bind(this.rootPane.widthProperty().divide(12));
        this.buttonSalir.prefHeightProperty().bind(this.rootPane.heightProperty().divide(10));

        // Adapta el botón que muestra la lista de los personajes a la resolución del dispositivo
        this.buttonLista.prefWidthProperty().bind(this.rootPane.widthProperty().divide(4));
        this.buttonLista.prefHeightProperty().bind(this.rootPane.heightProperty().divide(8));

        // Adapta el botón para regresar a las instrucciones a la resolución del dispositivo
        this.buttonRegresar.prefWidthProperty().bind(this.rootPane.widthProperty().divide(12));
        this.buttonRegresar.prefHeightProperty().bind(this.rootPane.heightProperty().divide(10));

        // Adapta la imagen de los personajes a la resolución del dispositivo
        this.personajeImage.fitWidthProperty().bind(this.rootPane.widthProperty().divide(4));
        this.personajeImage.fitHeightProperty().bind(this.rootPane.heightProperty().divide(2));

        // Carga el ícono del botón de salir
        Image imagenSalir = new Image(this.getClass().getResourceAsStream("/Instrucciones/Assets/regresar.png"));
        ImageView imageView = new ImageView(imagenSalir);
        imageView.setFitWidth((double)50.0F);
        imageView.setFitHeight((double)50.0F);
        this.buttonSalir.setGraphic(imageView);

        // Carga el ícono del botón para regresar a las instrucciones
        Image imagenRegresar = new Image(this.getClass().getResourceAsStream("/Instrucciones/Assets/regresar.png"));
        ImageView imgV = new ImageView(imagenRegresar);
        imgV.setFitWidth((double)50.0F);
        imgV.setFitHeight((double)50.0F);
        this.buttonRegresar.setGraphic(imgV);

        // Carga el ícono del botón para cambiar al personaje anterior de la lista
        Image imagenIzquierda = new Image(this.getClass().getResourceAsStream("/Instrucciones/Assets/flechaIzquierda.png"));
        ImageView imageView2 = new ImageView(imagenIzquierda);
        imageView2.setFitWidth((double)50.0F);
        imageView2.setFitHeight((double)50.0F);
        this.buttonIzquierda.setGraphic(imageView2);

        // Carga el ícono del botón para cambiar al personaje siguiente de la lista
        Image imagenDerecha = new Image(this.getClass().getResourceAsStream("/Instrucciones/Assets/flechaDerecha.png"));
        ImageView imageView3 = new ImageView(imagenDerecha);
        imageView3.setFitWidth((double)50.0F);
        imageView3.setFitHeight((double)50.0F);
        this.buttonDerecha.setGraphic(imageView3);
    }

    // Metodo para regresar al menú principal
    @FXML
    public void salir(ActionEvent e) {
        // Cargar el archivo FXML del menú y mostrarlo
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
        this.contentPane.setVisible(false); // Esconder el panel de las instrucciones
        this.contentPanePersonajes.setVisible(true); // Mostrar el panel de los personajes

        this.cargarPersonajes(); // Cargar las imagenes de los personajes
        this.actualizarPersonaje(); // Mostar el personaje en el índice actual
    }

    // Botón para cambiar al personaje anterior de la lista
    @FXML
    public void izquierda(ActionEvent e) {
        if (this.indiceActual > 0) {
            this.indiceActual--;
        } else {
            this.indiceActual = this.personajes.size()-1;
        }

        this.actualizarPersonaje(); // Metodo para mostrar la imagen del personaje actual
    }

    // Botón para cambiar al personaje siguiente de la lista
    @FXML
    public void derecha(ActionEvent e) {
        if (this.indiceActual < this.personajes.size()-1) {
            this.indiceActual++;
        } else {
            this.indiceActual = 0;
        }

        this.actualizarPersonaje(); // Metodo para mostrar la imagen del personaje actual
    }

    // Pide la lista de personajes a la base de datos, y la almacena en el atributo "personajes"
    private void cargarPersonajes() {
        this.personajes = PersonajeDB.getPersonajes();
    }

    // Cambia la imagen actual que tiene el panel de su descripción
    private void actualizarPersonaje() {
        this.personajeImage.setImage(personajes.get(indiceActual).getImagenFX());
        this.labelNombrePer.setText(this.personajes.get(this.indiceActual).getNombre());
        this.textAreaDescripcion.setText(this.personajes.get(this.indiceActual).getDescripcionString());
    }

    // Regresa al menú que muestra las instrucciones
    public void regresarInstrucciones(ActionEvent e) {
        this.contentPanePersonajes.setVisible(false);
        this.contentPane.setVisible(true);
    }

    // Metodos para reproducir los sonidos ambientales

    public void sonidoSplash(){
        sonidoSplash.setVolume(0.4);
        sonidoSplash.play();
    }

    public void sonidoGota(){
        sonidoGota.setVolume(0.2);
        sonidoGota.play();
    }
}
