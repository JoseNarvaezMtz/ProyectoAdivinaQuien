package Menu;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MenuController implements Initializable {

    @FXML private ImageView fondoImage;
    @FXML private Pane rootPane;
    @FXML private GridPane contentPane;

    @FXML private Button buttonJugar;
    @FXML private Button buttonCreditos;
    @FXML private Button buttonPartidas;
    @FXML private Button buttonInstrucciones;
    @FXML private Button buttonMusic;
    @FXML private Button buttonFondo;
    @FXML private Button buttonModo;
    @FXML private Button buttonSalir;
    @FXML private Button btnNickCancelar;
    @FXML private Button btnNickConfirmar;

    @FXML Pane darkness;
    @FXML GridPane GridPaneNickname;
    @FXML TextField TextFieldNickname;
    @FXML GridPane nickNameContainer;
    @FXML
    Label labelDigitos;

    private Stage stage;
    private Scene scene;
    private Parent root;

    public void bottonSalir(ActionEvent e){
        System.exit(0);
    }

    public void bottonCambiarModo(ActionEvent e){
        System.out.println("Cambio Modo");
        stage = (Stage)((Node) e.getSource()).getScene().getWindow();
        if(Menu.fullScreen){
            stage.setFullScreen(false);
            stage.setMinHeight(720);
            stage.setMinWidth(1280);
            Menu.fullScreen = false;
        }
        else{
            stage.setFullScreen(true);
            Menu.fullScreen = true;
        }
    }

    public void bottonCambiarFondo(ActionEvent e){
        System.out.println("Cambio Fondo");
    }

    public void bottonMusica(ActionEvent e){
        System.out.println("Apago musica");
    }

    public void cambiarCreditos(ActionEvent e) throws IOException {
        Parent root = (Parent) FXMLLoader.load(getClass().getResource("/Creditos/Creditos.fxml"));
        Scene scene = rootPane.getScene();
        scene.getStylesheets().add(getClass().getResource("/Creditos/CreditosStyles.css").toExternalForm());
        stage = (Stage)((Node) e.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.hide();
        stage.setScene(scene);
        stage.show();
    }

    public void cambiarInstrucciones(ActionEvent e) throws IOException {
        Parent nuevoRoot = FXMLLoader.load(getClass().getResource("/Instrucciones/Instrucciones.fxml"));
        Scene nuevaScene = new Scene(nuevoRoot);
        nuevaScene.getStylesheets().add(getClass().getResource("/Instrucciones/InstruccionesStyles.css").toExternalForm());

        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        stage.hide();
        stage.setScene(nuevaScene);
        stage.show();
    }

    public void iniciarSesion(ActionEvent e){
        darkness.setVisible(true);
        nickNameContainer.setVisible(true);
        contentPane.setDisable(true);
    }

    public void cancelarInicioSesion(){
        darkness.setVisible(false);
        nickNameContainer.setVisible(false);
        contentPane.setDisable(false);
    }

    public void partidasRegistradas(ActionEvent e) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/Partidas/Partidas.fxml"));
        Scene scene = new Scene(root);

        scene.getStylesheets().add(getClass().getResource("/Partidas/PartidasStyles.css").toExternalForm());

        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        stage.hide();
        stage.setScene(scene);
        stage.show();
    }

    public void cambiarSalaDeEspera(ActionEvent e) throws IOException {
        Menu.nickName = TextFieldNickname.getText();

        Parent nuevoRoot = FXMLLoader.load(getClass().getResource("/SalaDeEspera/SalaDeEspera.fxml"));
        Scene nuevaScene = new Scene(nuevoRoot);
        nuevaScene.getStylesheets().add(getClass().getResource("/SalaDeEspera/SalaDeEsperaStyles.css").toExternalForm());

        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        stage.hide();
        stage.setScene(nuevaScene);
        stage.show();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        javafx.application.Platform.runLater(() -> {
            Stage stage = (Stage) rootPane.getScene().getWindow();
            stage.setFullScreen(Menu.fullScreen);
        });

        //LISTENER que hace que el botón que se encuentra al iniciar sesión se ecuentre
        //deshabilitado hasta que tenga un length de 4
        TextFieldNickname.textProperty().addListener((observable, oldValue, newValue) -> {
            btnNickConfirmar.setDisable(newValue.length() <= 4 || newValue.length() >= 17);
            labelDigitos.setVisible(newValue.length() <= 4 || newValue.length() >= 17);
        });

        // Adaptar el fondo a la resolución del dispositivo
        fondoImage.fitWidthProperty().bind(rootPane.widthProperty());
        fondoImage.fitHeightProperty().bind(rootPane.heightProperty());

        // Adaptar el contenido a la resolución del dispotivo
        contentPane.prefWidthProperty().bind(rootPane.widthProperty());
        contentPane.prefHeightProperty().bind(rootPane.heightProperty());

        nickNameContainer.prefWidthProperty().bind(rootPane.widthProperty());
        nickNameContainer.prefHeightProperty().bind(rootPane.heightProperty());

        // Adaptar el panel que simula oscuridad a la resolución del sispositivo
        darkness.prefWidthProperty().bind(rootPane.widthProperty());
        darkness.prefHeightProperty().bind(rootPane.heightProperty());

        // Adaptar panel de nickname a la resolucion del dispositivo
        GridPaneNickname.prefWidthProperty().bind(rootPane.widthProperty().divide(4));
        GridPaneNickname.prefHeightProperty().bind(rootPane.heightProperty().divide(5));

        // Aadaptar los botones principales a la resolución del dispositivo
        buttonJugar.prefWidthProperty().bind(rootPane.widthProperty().divide(7));
        buttonJugar.prefHeightProperty().bind(rootPane.heightProperty().divide(10));

        buttonCreditos.prefWidthProperty().bind(rootPane.widthProperty().divide(7));
        buttonCreditos.prefHeightProperty().bind(rootPane.heightProperty().divide(10));

        buttonInstrucciones.prefWidthProperty().bind(rootPane.widthProperty().divide(7));
        buttonInstrucciones.prefHeightProperty().bind(rootPane.heightProperty().divide(10));

        buttonPartidas.prefWidthProperty().bind(rootPane.widthProperty().divide(7));
        buttonPartidas.prefHeightProperty().bind(rootPane.heightProperty().divide(10));

        // Adaptar los botones secundarios a la resolución del dispositivo
        buttonMusic.prefWidthProperty().bind(rootPane.widthProperty().divide(12));
        buttonMusic.prefHeightProperty().bind(rootPane.heightProperty().divide(11));

        buttonFondo.prefWidthProperty().bind(rootPane.widthProperty().divide(12));
        buttonFondo.prefHeightProperty().bind(rootPane.heightProperty().divide(11));

        buttonModo.prefWidthProperty().bind(rootPane.widthProperty().divide(12));
        buttonModo.prefHeightProperty().bind(rootPane.heightProperty().divide(11));

        buttonSalir.prefWidthProperty().bind(rootPane.widthProperty().divide(12));
        buttonSalir.prefHeightProperty().bind(rootPane.heightProperty().divide(11));

        // Adaptar los botones del panel de ingresar nickname a la resolución del dispositivo

        btnNickCancelar.prefWidthProperty().bind(rootPane.widthProperty().divide(17));
        btnNickCancelar.prefHeightProperty().bind(rootPane.heightProperty().divide(16));

        btnNickConfirmar.prefWidthProperty().bind(rootPane.widthProperty().divide(17));
        btnNickConfirmar.prefHeightProperty().bind(rootPane.heightProperty().divide(16));

        Image imagenSalir = new Image(getClass().getResourceAsStream("/Menu/Assets/salir.png"));
        ImageView imageView = new ImageView(imagenSalir);
        imageView.setFitWidth(45);
        imageView.setFitHeight(45);
        buttonSalir.setGraphic(imageView);

        Image imagenMusica = new Image(getClass().getResourceAsStream("/Menu/Assets/musica.png"));
        ImageView imageView2 = new ImageView(imagenMusica);
        imageView2.setFitWidth(45);
        imageView2.setFitHeight(45);
        buttonMusic.setGraphic(imageView2);

        Image imagenFondo = new Image(getClass().getResourceAsStream("/Menu/Assets/fondo.png"));
        ImageView imageView3 = new ImageView(imagenFondo);
        imageView3.setFitWidth(45);
        imageView3.setFitHeight(45);
        buttonFondo.setGraphic(imageView3);

        Image imagenModo = new Image(getClass().getResourceAsStream("/Menu/Assets/maximizar.png"));
        ImageView imageView4 = new ImageView(imagenModo);
        imageView4.setFitWidth(40);
        imageView4.setFitHeight(40);
        buttonModo.setGraphic(imageView4);

        Image imagenCancelar = new Image(getClass().getResourceAsStream("/Menu/Assets/cancelar.png"));
        ImageView imageView5 = new ImageView(imagenCancelar);
        imageView5.setFitWidth(20);
        imageView5.setFitHeight(20);
        btnNickCancelar.setGraphic(imageView5);

        Image imagenOk = new Image(getClass().getResourceAsStream("/Menu/Assets/ok.png"));
        ImageView imageView6 = new ImageView(imagenOk);
        imageView6.setFitWidth(30);
        imageView6.setFitHeight(30);
        btnNickConfirmar.setGraphic(imageView6);
    }
}