package Menu;

import Sockets.Cliente;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import javax.swing.event.ChangeListener;
import java.io.IOException;
import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;

public class MenuController implements Initializable {

    // PANELES
    @FXML private Pane rootPane;
    @FXML private GridPane contentPane;
    @FXML Pane darkness;
    @FXML GridPane GridPaneNickname;
    @FXML GridPane nickNameContainer;
    @FXML Pane titlePane;
    @FXML Label labelTitulo1;
    @FXML Label labelTitulo2;

    // BOTONES
    @FXML private Button buttonJugar;
    @FXML private Button buttonCreditos;
    @FXML private Button buttonPartidas;
    @FXML private Button buttonInstrucciones;
    @FXML private Button buttonMusic;
    @FXML private Button buttonCambiarMusica;
    @FXML private Button buttonModo;
    @FXML private Button buttonSalir;
    @FXML private Button btnNickCancelar;
    @FXML private Button btnNickConfirmar;

    // IMAGEVIEWS
    @FXML private ImageView fondoImage;

    // LABELS
    @FXML Label labelDigitos;

    // tEXTFIELDS
    @FXML TextField TextFieldNickname;

    private Stage stage;
    private Scene scene;
    private Parent root;

    private Cliente cliente;

    //Musica
    public static MediaPlayer musica;
    private static AudioClip sonidoMadera;
    private static AudioClip sonidoGaviota;
    public static AudioClip sonidoTeclado;

    public static Boolean desicionUsuario = true;
    public final static String musicas[] = {"Music1", "Music2", "Music3", "Music4", "Music5", "Music6", "Music7"};
    private int indiceActual = 0; //Indice para las músicas aleatorias

    // METODO QUE SE EJECUTA AL INICIALIZAR LA PANTALLA
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // Inicializando sonido
        sonidoMadera = new AudioClip(getClass().getResource("/Menu/Assets/woodHit.mp3").toString());
        sonidoGaviota = new AudioClip(getClass().getResource("/Menu/Assets/seagull.mp3").toString());
        sonidoTeclado = new AudioClip(getClass().getResource("/Menu/Assets/keyboard.wav").toString());

        // Inicializando música
        inicializarMusica();

        // ADAPTA
        javafx.application.Platform.runLater(() -> {
            Stage stage = (Stage) rootPane.getScene().getWindow();
            stage.setFullScreen(Menu.fullScreen);
        });

        // DESHABILITAMOS EL BOTON DE CONFIRMAR NICKNAME HASTA QUE TENGA MÁS DE 3 CARACTERES, Y MENOS DE 18
        TextFieldNickname.textProperty().addListener((observable, oldValue, newValue) -> {
            btnNickConfirmar.setDisable(newValue.length() <= 4 || newValue.length() >= 17);
            labelDigitos.setVisible(newValue.length() <= 4 || newValue.length() >= 17);
        });

        // ADAPTACIÓN DE TAMAÑOS A LA RESOLUCIÓN DE PANTALLA

        // Adaptar el fondo a la resolución del dispositivo
        fondoImage.fitWidthProperty().bind(rootPane.widthProperty());
        fondoImage.fitHeightProperty().bind(rootPane.heightProperty());

        // Adaptar el contenido a la resolución del dispotivo
        contentPane.prefWidthProperty().bind(rootPane.widthProperty());
        contentPane.prefHeightProperty().bind(rootPane.heightProperty());

        // ADAPTAR EL PANEL QUE TIENE LA PETICIÓN DEL NICKNAME DEL USUARIO
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

        // ADAPTAR EL TAMAÑO DEL BOTÓN PARA IR A LOS CRÉDITOS A LA RESOLUCIÓN DEL DISPOSITIVO
        buttonCreditos.prefWidthProperty().bind(rootPane.widthProperty().divide(7));
        buttonCreditos.prefHeightProperty().bind(rootPane.heightProperty().divide(10));

        // ADAPTAR EL TAMAÑO DEL BOTÓN PARA IR A LAS INSTRUCCIONES A LA RESOLUCIÓN DEL DISPOSITIVO
        buttonInstrucciones.prefWidthProperty().bind(rootPane.widthProperty().divide(6));
        buttonInstrucciones.prefHeightProperty().bind(rootPane.heightProperty().divide(10));

        // ADAPTAR EL TAMAÑO DEL BOTÓN PARA IR A LAS PARTIDAS REGISTRADAS A LA RESOLUCIÓN DEL DISPOSITIVO
        buttonPartidas.prefWidthProperty().bind(rootPane.widthProperty().divide(7));
        buttonPartidas.prefHeightProperty().bind(rootPane.heightProperty().divide(10));

        // ADAPTAR EL BOTÓN PARA ACTIVAR/RESACTIVAR MÚSICA A LA RESOLUCIÓN DEL DISPOSITIVO
        buttonMusic.prefWidthProperty().bind(rootPane.widthProperty().divide(12));
        buttonMusic.prefHeightProperty().bind(rootPane.heightProperty().divide(11));

        // ADAPTAR BOTÓN PARA CAMBIAR DE FONDO A LA RESOLUCIÓN DEL DISPOSITIVO
        buttonCambiarMusica.prefWidthProperty().bind(rootPane.widthProperty().divide(12));
        buttonCambiarMusica.prefHeightProperty().bind(rootPane.heightProperty().divide(11));

        // ADAPTAR BOTÓN PARA CAMBIAR ENTRE PANTALLA COMPLETA Y VENTANA A LA RESOLUCIÓN DEL DISPOSITIVO
        buttonModo.prefWidthProperty().bind(rootPane.widthProperty().divide(12));
        buttonModo.prefHeightProperty().bind(rootPane.heightProperty().divide(11));

        // ADAPTAR EL BOTÓN DE SALIR A LA RESOLUCIÓN DEL DISPOSITIVO
        buttonSalir.prefWidthProperty().bind(rootPane.widthProperty().divide(12));
        buttonSalir.prefHeightProperty().bind(rootPane.heightProperty().divide(11));

        // ADAPTAR BOTÓN PARA CANCELAR EL INICIO DE SESIÓN A LA REOSLUCIÓN DEL DISPOSITIVO
        btnNickCancelar.prefWidthProperty().bind(rootPane.widthProperty().divide(17));
        btnNickCancelar.prefHeightProperty().bind(rootPane.heightProperty().divide(16));

        // ADAPTAR EL BOTÓN PARA CONFIRMAR NICKNAME A LA RESOLUCIÓN DEL DISPOSITIVO
        btnNickConfirmar.prefWidthProperty().bind(rootPane.widthProperty().divide(17));
        btnNickConfirmar.prefHeightProperty().bind(rootPane.heightProperty().divide(16));

        // ADAPTAR EL PANE DEL TITULO A LA RESOLUCIÓN DEL DISPOSITIVO
        titlePane.prefWidthProperty().bind(rootPane.widthProperty().divide(3));
        titlePane.prefHeightProperty().bind(rootPane.heightProperty().divide(1.1));

        // Adaptamos los labels del titulo a la resolución del dispositivo
        labelTitulo1.prefWidthProperty().bind(titlePane.widthProperty());
        labelTitulo1.prefHeightProperty().bind(titlePane.heightProperty());

        Font fontTitulo = new Font(80);
        labelTitulo1.setFont(fontTitulo);
        labelTitulo2.setFont(fontTitulo);

        labelTitulo2.prefWidthProperty().bind(titlePane.widthProperty());
        labelTitulo2.prefHeightProperty().bind(titlePane.heightProperty());

        // CARGA DE ÍCONOS

        // CREACIÓN Y CARGA DEL ÍCONO PARA EL BOTÓN DE SALIR
        Image imagenSalir = new Image(getClass().getResourceAsStream("/Menu/Assets/salir.png"));
        ImageView imageView = new ImageView(imagenSalir);
        imageView.setFitWidth(45);
        imageView.setFitHeight(45);
        buttonSalir.setGraphic(imageView);

        // CREACIÓN Y CARGA DEL ÍCONO PARA EL BOTÓN DE ACTIVAR/DESACTIVAR MÚSICA
        if(desicionUsuario){
            Image imagenMusica = new Image(getClass().getResourceAsStream("/Menu/Assets/musica.png"));
            ImageView imageView2 = new ImageView(imagenMusica);
            imageView2.setFitWidth(45);
            imageView2.setFitHeight(45);
            buttonMusic.setGraphic(imageView2);
        } else{
            Image imagenMusica = new Image(getClass().getResourceAsStream("/Menu/Assets/musicaMuteada.png"));
            ImageView imageView2 = new ImageView(imagenMusica);
            imageView2.setFitWidth(45);
            imageView2.setFitHeight(45);
            buttonMusic.setGraphic(imageView2);
        }

        // CREACIÓN Y CARGA DEL ÍCONO PARA EL BOTÓN DE CAMBIAR EL FONDO
        Image imagenFondo = new Image(getClass().getResourceAsStream("/Menu/Assets/cambiarMusica.png"));
        ImageView imageView3 = new ImageView(imagenFondo);
        imageView3.setFitWidth(45);
        imageView3.setFitHeight(45);
        buttonCambiarMusica.setGraphic(imageView3);

        // CREACIÓN Y CARGA DEL ÍCONO PARA EL BOTÓN DE SALIR
        Image imagenModo = new Image(getClass().getResourceAsStream("/Menu/Assets/maximizar.png"));
        ImageView imageView4 = new ImageView(imagenModo);
        imageView4.setFitWidth(40);
        imageView4.setFitHeight(40);
        buttonModo.setGraphic(imageView4);

        // CREACIÓN Y CARGA DEL ÍCONO PARA EL BOTÓN DE CANCELAR EL INGRESO DEL NICKNAME
        Image imagenCancelar = new Image(getClass().getResourceAsStream("/Menu/Assets/cancelar.png"));
        ImageView imageView5 = new ImageView(imagenCancelar);
        imageView5.setFitWidth(20);
        imageView5.setFitHeight(20);
        btnNickCancelar.setGraphic(imageView5);

        // CREACIÓN Y CARGA DEL ÍCONO PARA EL BOTÓN DE CANCELAR EL INGRESO DEL NICKNAME
        Image imagenOk = new Image(getClass().getResourceAsStream("/Menu/Assets/ok.png"));
        ImageView imageView6 = new ImageView(imagenOk);
        imageView6.setFitWidth(30);
        imageView6.setFitHeight(30);
        btnNickConfirmar.setGraphic(imageView6);
    }

    // METODO PARA SALIR DE LA APLICACIÓN
    public void bottonSalir(ActionEvent e){
        System.exit(0);
    }

    // METODO QUE CAMBIA ENTRE PANTALLA COMPLETA Y PANTALLA EN VENTANA
    public void bottonCambiarModo(ActionEvent e){
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

    // BOTÓN PARA SILENCIAR/ACTIVAR MÚSICA
    public void bottonMusica(ActionEvent e){
        musica.setMute(!musica.isMute());
        if(desicionUsuario == true){
            desicionUsuario = false;
            Image img = new Image(getClass().getResourceAsStream("/Menu/Assets/musicaMuteada.png"));
            ImageView imgV = new ImageView(img);
            imgV.setFitWidth(45);
            imgV.setFitHeight(45);
            buttonMusic.setGraphic(imgV);
        }
        else{
            desicionUsuario = true;
            Image img = new Image(getClass().getResourceAsStream("/Menu/Assets/musica.png"));
            ImageView imgV = new ImageView(img);
            imgV.setFitWidth(45);
            imgV.setFitHeight(45);
            buttonMusic.setGraphic(imgV);
        }
    }

    // BOTÓN PARA IR A LA PANTALLA DE CRÉDITOS
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

    // BOTÓN PARA IR A LA PANTALLA DE INSTRUCCIONES
    public void cambiarInstrucciones(ActionEvent e) throws IOException {
        Parent nuevoRoot = FXMLLoader.load(getClass().getResource("/Instrucciones/Instrucciones.fxml"));
        Scene nuevaScene = new Scene(nuevoRoot);
        nuevaScene.getStylesheets().add(getClass().getResource("/Instrucciones/InstruccionesStyles.css").toExternalForm());
        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        stage.hide();
        stage.setScene(nuevaScene);
        stage.show();
    }

    // BOTÓN QUE ACTIVA EL SUBMENÚ PARA INGRESAR EL NICKNAME DEL USUARIO
    public void iniciarSesion(ActionEvent e){
        darkness.setVisible(true);
        nickNameContainer.setVisible(true);
        TextFieldNickname.clear();
    }


    // BOTÓN PARA DESACTIVAR EL SUBMENU PARA INGRESAR EL NICKNAME DEL USUARIO
    public void cancelarInicioSesion(){
        darkness.setVisible(false);
        nickNameContainer.setVisible(false);
        contentPane.setDisable(false);
    }

    // BOTÓN PARA IR A LA PANTALLA DE PARTIDAS REGISTRADAS
    public void partidasRegistradas(ActionEvent e) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/Partidas/Partidas.fxml"));
        Scene scene = new Scene(root);

        scene.getStylesheets().add(getClass().getResource("/Partidas/PartidasStyles.css").toExternalForm());

        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        stage.hide();
        stage.setScene(scene);
        stage.show();
    }

    // BOTÓN PARA IR A LA PANTALLA DE LA SALA DE ESPERA
    public void cambiarSalaDeEspera(ActionEvent e) throws IOException {
        // Solo se intenta acceder para añadir el nickname si esta desde el menu
        if (this.TextFieldNickname != null) {
            if (!TextFieldNickname.getText().trim().isEmpty()) {
                Menu.nickName = TextFieldNickname.getText().trim();
            }
        }

        Parent nuevoRoot = FXMLLoader.load(getClass().getResource("/SalaDeEspera/SalaDeEspera.fxml"));
        Scene nuevaScene = new Scene(nuevoRoot);
        nuevaScene.getStylesheets().add(getClass().getResource("/SalaDeEspera/SalaDeEsperaStyles.css").toExternalForm());

        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        musica.setMute(!musica.isMute());
        stage.hide();
        stage.setScene(nuevaScene);
        stage.show();
    }

    // Metodos de sonido de al seleccionar una opción
    public void sonidoSeleccion(){
        sonidoMadera.setVolume(0.05);
        sonidoMadera.play();
    }

    public void sonidoGaviota(){
        sonidoGaviota.setVolume(0.2);
        sonidoGaviota.play();
    }

    public void sonidoTeclado(){
        sonidoTeclado.setVolume(0.1);
        sonidoTeclado.play();
    }

    //botton Pata Cambiar Canción

    public void ButtonSiguienteCancion() {
        if (musica != null) {
            musica.stop();
        }
        // avanza a la siguiente posición
        // toma el residuo para que sea "Circular"
        indiceActual = (indiceActual + 1) % musicas.length;

        // Ponemos la nueva canción
        String nueva = musicas[indiceActual];
        Media media = new Media(getClass().getResource("/Menu/Assets/" + nueva + ".mp3").toString());
        musica = new MediaPlayer(media);
        musica.setCycleCount(MediaPlayer.INDEFINITE);
        musica.setVolume(0.2);

        //Si el usuario tiene la música muteada no va a sonar
        musica.setMute(!desicionUsuario);
        musica.play();
    }

    public void inicializarMusica(){
        // Si no se está reproduciendo nada, reproduce la música, es para evitar conflictos cada que se instancie la scene
        //La elegimos de la lista de manera aleatoria
        if (musica == null) {
            Random rand = new Random();
            indiceActual = rand.nextInt(musicas.length); //Guarda el indice para la siguiente canción
            int indice = rand.nextInt(musicas.length);
            String seleccion = musicas[indice];

            Media music = new Media(getClass().getResource("/Menu/Assets/" + seleccion + ".mp3").toString());
            musica = new MediaPlayer(music);
            musica.setCycleCount(MediaPlayer.INDEFINITE);
        }

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