package Partidas;

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
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class PartidasController implements Initializable {

    @FXML private ImageView fondoImage;
    @FXML private Pane rootPane;

    @FXML GridPane gridPane;

    @FXML Button buttonSalir;

    @FXML TableView tableroPartidas;


    private Stage stage;
    private Scene scene;
    private Parent root;

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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        javafx.application.Platform.runLater(() -> {
            if (rootPane.getScene() != null) {
                Stage stage = (Stage) rootPane.getScene().getWindow();
                stage.setFullScreen(Menu.fullScreen);
            }
        });

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
        String[] titulos = { "Usuario 1", "Usuario 2", "Fecha", "Tiempo", "Ganador" };

        // Crea la matriz con el tamaño de columnas que tendrá la tabla

        for (int i = 0; i < titulos.length; i++) {
            final int colIndex = i;
            TableColumn<ObservableList<String>, String> columna = new TableColumn<>(titulos[i]);
            columna.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(colIndex)));
            tableroPartidas.getColumns().add(columna);
        }

        // Crear datos manualmente, PARTE DE JOSÉ LUIS
        // CAMBIAR CON LOS DATOS DE LA BASE DE DATOS
        // Instancia de los Strings que tendrá el array list de las columnas
        ObservableList<ObservableList<String>> datos = FXCollections.observableArrayList();
        // Instancia de los objetos que se agreguen de base de datos
        ObservableList<String> fila1 = FXCollections.observableArrayList("Jose", "Luis", "2025-06-01", "10:00", "ya");
        // Cargar los datos en el array list de filas
        datos.addAll(fila1);
        //carga la tabla con los datos
        tableroPartidas.setItems(datos);

        // Imágenes de los botones que tenga la scene
        Image imagenSalir = new Image(getClass().getResourceAsStream("/Partidas/Assets/regresar.png"));
        ImageView imageView = new ImageView(imagenSalir);
        imageView.setFitWidth(45);
        imageView.setFitHeight(45);
        buttonSalir.setGraphic(imageView);
    }
}
