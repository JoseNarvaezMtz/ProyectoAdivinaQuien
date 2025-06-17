package Partidas;

import DataBaseClasses.PartidaDB;
import Menu.Menu;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.media.AudioClip;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class PartidasController implements Initializable {

    @FXML private ImageView fondoImage;
    @FXML private Pane rootPane;

    @FXML GridPane gridPane;
    @FXML Pane panelLabel;

    @FXML Label labelTitulo;
    @FXML TextField textFieldBuscarPorUsuario;

    @FXML Button buttonSalir;

    @FXML TableView tableroPartidas;


    private Stage stage;
    private Scene scene;
    private Parent root;

    private static AudioClip sonidoArena;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        sonidoArena = new AudioClip(getClass().getResource("/Partidas/Assets/sand.mp3").toString());

        javafx.application.Platform.runLater(() -> {
            if (rootPane.getScene() != null) {
                Stage stage = (Stage) rootPane.getScene().getWindow();
                stage.setFullScreen(Menu.fullScreen);
            }
        });

        // Adaptar el pane al tamaño del rootPane
        panelLabel.prefWidthProperty().bind(rootPane.widthProperty().divide(1.5));
        panelLabel.prefHeightProperty().bind(rootPane.heightProperty().divide(6));

        // Hacer que el label sea mas grande
        labelTitulo.getStyleClass().add("labelTitulo");
        labelTitulo.prefWidthProperty().bind(panelLabel.widthProperty());
        labelTitulo.prefHeightProperty().bind(panelLabel.heightProperty());

        // Apatar el textAreaPane al tamaño del rootpane
        textFieldBuscarPorUsuario.getStyleClass().add("textFieldBuscarPorUsuario");
        textFieldBuscarPorUsuario.prefWidthProperty().bind(rootPane.widthProperty().divide(15));
        textFieldBuscarPorUsuario.prefHeightProperty().bind(rootPane.heightProperty().divide(18));

        // Adaptar el gridPane al tamaño del rootPane
        gridPane.prefWidthProperty().bind(rootPane.widthProperty());
        gridPane.prefHeightProperty().bind(rootPane.heightProperty());

        // Adaptar la imagen de fondo al tamaño del rootPane
        fondoImage.fitWidthProperty().bind(rootPane.widthProperty());
        fondoImage.fitHeightProperty().bind(rootPane.heightProperty());

        // Adaptar el tablero que muestra los datos de las partidas
        tableroPartidas.prefWidthProperty().bind(gridPane.widthProperty().multiply(0.9));
        tableroPartidas.prefHeightProperty().bind(gridPane.heightProperty().multiply(0.9));

        // Crear los títulos que tendrán las columnas de la tabla mediante un array de Strings
        String[] titulos = { "Jugador 1", "Jugador 2", "Ganador", "Personaje Ganador", "Fecha", "Duración Partida"};

        // Crea la matriz con el tamaño de columnas que tendrá la tabla
        for (int i = 0; i < titulos.length; i++) {
            int colIndex = i;
            TableColumn<ObservableList<String>, String> columna = new TableColumn<>(titulos[i]);
            columna.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(colIndex)));
            tableroPartidas.getColumns().add(columna);
        }

        //Manda a llamar la función con los datos que se llenará la tabla
        llenarDatos();

        // Imágenes de los botones que tenga la scene
        Image imagenSalir = new Image(getClass().getResourceAsStream("/Partidas/Assets/regresar.png"));
        ImageView imageView = new ImageView(imagenSalir);
        imageView.setFitWidth(45);
        imageView.setFitHeight(45);
        buttonSalir.setGraphic(imageView);

        textFieldBuscarPorUsuario.textProperty().addListener((observable, oldValue, newValue) -> {
            tableroPartidas.setItems(PartidaDB.getPorNombre(textFieldBuscarPorUsuario.getText()));
        });
    }

    // Función que cambia al menú principal
    public void salir(ActionEvent e){
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

    //CON ESTAS FUNCIONES JOSE LUIS TIENE QUE MOSTRAR LA LISTA
    public void buscarUsuario(ActionEvent e){
        //AQUI TIENES QUE PONER TU MÉTODO PARA BUSCAR AL USUARIO JOSÉ
        //NO SE TE OLVODE QUE TIENES QUE ACTUALIZAR LA TABLA PARA QUE MUESTRE
        //LAS PARTIDAS CON ESE USUARIO
        llenarDatos();
    }

    //FUNCIÓN PARA QUE MANDES A LLAMAR EN BUSCAR USUARIOS
    public void llenarDatos(){
        tableroPartidas.setItems(PartidaDB.getHistorialPartidas());
    }

    public void sonidoSeleccion(){
        sonidoArena.setVolume(0.2);
        sonidoArena.play();
    }

    public void ordenarPorDuracion(){
        tableroPartidas.setItems(PartidaDB.getHistorialOrdenado());
    }
}