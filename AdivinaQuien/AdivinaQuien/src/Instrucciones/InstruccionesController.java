package Instrucciones;

import Menu.Menu;
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
import javafx.scene.layout.Region;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class InstruccionesController implements Initializable {

    @FXML private Pane rootPane;
    @FXML private ImageView fondoImage;
    @FXML private GridPane contentPane;
    @FXML private TextArea instrucciones;
    @FXML private Button buttonSalir;
    @FXML private Button buttonLista;
    @FXML private Label Titulo;
    @FXML private GridPane contentPanePersonajes;
    @FXML private ImageView personajeImage;
    @FXML private Button buttonIzquierda;
    @FXML private Button buttonDerecha;

    private List<Image> imagenes = new ArrayList<>();
    private int indiceActual = 0;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        Platform.runLater(() -> {
            Stage stage = (Stage) rootPane.getScene().getWindow();
            if (stage != null) {
                stage.setFullScreen(Menu.fullScreen);
            }
        });

        // Fondo adaptable
        fondoImage.fitWidthProperty().bind(rootPane.widthProperty());
        fondoImage.fitHeightProperty().bind(rootPane.heightProperty());

        // Contenedor adaptable
        contentPane.prefWidthProperty().bind(rootPane.widthProperty());
        contentPane.prefHeightProperty().bind(rootPane.heightProperty());

        contentPanePersonajes.prefWidthProperty().bind(rootPane.widthProperty());
        contentPanePersonajes.prefHeightProperty().bind(rootPane.heightProperty());

        // Elementos adaptable
        Titulo.prefWidthProperty().bind(rootPane.widthProperty().multiply(0.85));
        Titulo.prefHeightProperty().bind(rootPane.heightProperty().multiply(0.1));

        instrucciones.prefWidthProperty().bind(rootPane.widthProperty().multiply(0.85));
        instrucciones.prefHeightProperty().bind(rootPane.heightProperty().multiply(0.6));

        buttonSalir.prefWidthProperty().bind(rootPane.widthProperty().divide(5));
        buttonSalir.prefHeightProperty().bind(rootPane.heightProperty().divide(10));

        buttonLista.prefWidthProperty().bind(rootPane.widthProperty().divide(4));
        buttonLista.prefHeightProperty().bind(rootPane.heightProperty().divide(8));

        rootPane.widthProperty().addListener((obs, oldVal, newVal) -> ajustarFuentes());
        rootPane.heightProperty().addListener((obs, oldVal, newVal) -> ajustarFuentes());

        ajustarFuentes();

        Image imagenSalir = new Image(getClass().getResourceAsStream("/Instrucciones/Assets/regresar.png"));
        ImageView imageView = new ImageView(imagenSalir);
        imageView.setFitWidth(50);
        imageView.setFitHeight(50);
        buttonSalir.setGraphic(imageView);

        Image imagenIzquierda = new Image(getClass().getResourceAsStream("/Instrucciones/Assets/flechaIzquierda.png"));
        ImageView imageView2 = new ImageView(imagenIzquierda);
        imageView2.setFitWidth(50);
        imageView2.setFitHeight(50);
        buttonIzquierda.setGraphic(imageView2);

        Image imagenDerecha = new Image(getClass().getResourceAsStream("/Instrucciones/Assets/flechaDerecha.png"));
        ImageView imageView3 = new ImageView(imagenDerecha);
        imageView3.setFitWidth(50);
        imageView3.setFitHeight(50);
        buttonDerecha.setGraphic(imageView3);

    }

    private void ajustarFuentes() {
        double ancho = rootPane.getWidth();
        double alto = rootPane.getHeight();
        double escala = Math.min(ancho / 1280, alto / 720);

        instrucciones.setFont(new Font("Arial", 24 * escala));
        buttonLista.setFont(new Font("Arial", 17 * escala));
        Titulo.setFont(new Font("Chiller", 48 * escala));
        Titulo.setAlignment(javafx.geometry.Pos.CENTER);


    }


    @FXML
    public void salir(ActionEvent e) {
        try {
            Parent menuRoot = FXMLLoader.load(getClass().getResource("/Menu/Menu.fxml"));
            Scene scene = new Scene(menuRoot);
            scene.getStylesheets().add(getClass().getResource("/Menu/MenuStyles.css").toExternalForm());

            Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            stage.hide();
            stage.setScene(scene);
            stage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    public void menuPersonajesInstrucciones(ActionEvent e) {
        contentPane.setVisible(false);
        contentPanePersonajes.setVisible(true);

        cargarImagenes();
        cambiarImagen();
    }

    private void cargarImagenes() {
        imagenes.clear();
        for (int i = 1; i < 5; i++) {
            String ruta = "/Instrucciones/Assets/Personajes/cat" + i + ".jpg";
            URL recurso = getClass().getResource(ruta);
            if (recurso != null) {
                Image img = new Image(recurso.toExternalForm());
                imagenes.add(img);
            } else {
                System.out.println("No se encontró: " + ruta);
            }
        }
    }

    private void cambiarImagen() {
        personajeImage.setImage(imagenes.get(indiceActual));
    }

    //esta madre va a la imagen anterior
    @FXML
    public void izquierda(ActionEvent e){
        if(indiceActual > 0){
            indiceActual--;
        }
        else{
            indiceActual = imagenes.size() - 1;
        }
        cambiarImagen();
    }

    //y esta otra chingadera va a la q sigue
    @FXML
    public void derecha(ActionEvent e){
        if(indiceActual < imagenes.size() - 1){
            indiceActual++;
        }
        else{
            indiceActual = 0;
        }
        cambiarImagen();
    }

    public void regresarInstrucciones(ActionEvent e){
        contentPanePersonajes.setVisible(false);
        contentPane.setVisible(true);
    }
}
