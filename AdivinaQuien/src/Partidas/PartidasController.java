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
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

// Clase controladora de la pantalla de partidas registradas

public class PartidasController implements Initializable {

    // Paneles
    @FXML private Pane rootPane;
    @FXML Pane panelLabel;
    @FXML GridPane gridPane;

    // ImageViews
    @FXML private ImageView fondoImage;

    // Labels
    @FXML Label labelTitulo;

    // Botones
    @FXML Button buttonSalir;
    @FXML Button ordenarPorDuracion;

    // TextFields
    @FXML TextField textFieldBuscarPorUsuario;

    // TableViews
    @FXML TableView tableroPartidas;

    // Audios de la ventana
    private static AudioClip sonidoArena;
    private static AudioClip sonidoTeclado;
    public static AudioClip sonidoEnviar;

    // Metodo que se ejecuta al cargar la escena
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // Cargar los audios utilizados en la escena
        sonidoArena = new AudioClip(getClass().getResource("/Partidas/Assets/sand.mp3").toString());
        sonidoTeclado = new AudioClip(getClass().getResource("/Partidas/Assets/keyboard.wav ").toString());
        sonidoEnviar = new AudioClip(getClass().getResource("/Partidas/Assets/send.mp3").toString());

        // Adaptar la aplicación a pantalla completa o ventana según indica la variable auxiliar estática de la clase Menu
        javafx.application.Platform.runLater(() -> {
            if (rootPane.getScene() != null) {
                Stage stage = (Stage) rootPane.getScene().getWindow();
                stage.setFullScreen(Menu.fullScreen);
            }
        });

        // Adaptar el panel del título de la escena a la resolución del dispositivo
        panelLabel.prefWidthProperty().bind(rootPane.widthProperty().divide(1.5));
        panelLabel.prefHeightProperty().bind(rootPane.heightProperty().divide(6));

        // Hacer que el label del título sea mas grande y asignarle una clase para sus estilos
        labelTitulo.getStyleClass().add("labelTitulo");
        labelTitulo.prefWidthProperty().bind(panelLabel.widthProperty());
        labelTitulo.prefHeightProperty().bind(panelLabel.heightProperty());

        // Apatar el textAreaPane a la resolución del dispositivo y asignarle una clase para sus estilos
        textFieldBuscarPorUsuario.getStyleClass().add("textFieldBuscarPorUsuario");
        textFieldBuscarPorUsuario.prefWidthProperty().bind(rootPane.widthProperty().divide(15));
        textFieldBuscarPorUsuario.prefHeightProperty().bind(rootPane.heightProperty().divide(18));

        // Adaptar el gridPane a la resolución del dispositivo
        gridPane.prefWidthProperty().bind(rootPane.widthProperty());
        gridPane.prefHeightProperty().bind(rootPane.heightProperty());

        // Adaptar la imagen de fondo a la resolución del dispositivo
        fondoImage.fitWidthProperty().bind(rootPane.widthProperty());
        fondoImage.fitHeightProperty().bind(rootPane.heightProperty());

        // Adaptar el tablero que muestra los datos de las partidas a la resolución del dispositivo
        tableroPartidas.prefWidthProperty().bind(gridPane.widthProperty().multiply(0.9));
        tableroPartidas.prefHeightProperty().bind(gridPane.heightProperty().multiply(0.9));

        // Adaptar el botón de ordenar por duración a la resolución del dispositivo
        ordenarPorDuracion.prefWidthProperty().bind(rootPane.widthProperty().divide(8));
        ordenarPorDuracion.prefHeightProperty().bind(rootPane.heightProperty().divide(13));

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

        // Cargar el ícono del botón para volver al menú
        Image imagenSalir = new Image(getClass().getResourceAsStream("/Partidas/Assets/regresar.png"));
        ImageView imageView = new ImageView(imagenSalir);
        imageView.setFitWidth(45);
        imageView.setFitHeight(45);
        buttonSalir.setGraphic(imageView);

        // Listener que escucha a cada tecleo en el textFieldBuscarPorUsuario
        textFieldBuscarPorUsuario.textProperty().addListener((observable, oldValue, newValue) -> {
            // Al detectar tecleo, actualiza la tabla con el metodo getPorNombre que filtra las partidas por nombre de usuario
            tableroPartidas.setItems(FXCollections.observableArrayList(PartidaDB.getPorNombre(textFieldBuscarPorUsuario.getText())));
        });
    }

    // Metodo para volver al menú principal
    public void salir(ActionEvent e){
        try {
            // Cargar el archivo FXML del menú principal y mostrarlo
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

    // Metodo para llenar la tabla con los datos de las partidas registradas
    public void llenarDatos(){
        tableroPartidas.setItems(PartidaDB.getHistorialPartidas());
    }

    // Metodo que reproduce el sonido ambiental al dar click
    public void sonidoSeleccion(){
        sonidoArena.setVolume(0.2);
        sonidoArena.play();
    }

    // Metodo que reproduce el sonido ambiental al teclear
    public void sonidoTeclado(){
        sonidoTeclado.setVolume(0.2);
        sonidoTeclado.play();
    }

    // Metodo que reproduce el sonido ambiental al dar click al botón de ordenar por duración
    public void sonidoEnviar(){
        sonidoEnviar.setVolume(0.2);
        sonidoEnviar.play();
    }

    // Metodo que actualiza la tabla a los datos ordenados por duración recibidos de la base de datos
    public void ordenarPorDuracion(){
        tableroPartidas.setItems(PartidaDB.getHistorialOrdenado());
    }
}