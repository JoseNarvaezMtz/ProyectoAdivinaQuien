package Menu;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;

// Clase controlador de la pantalla del menú principal

public class MenuController implements Initializable {

    // Paneles
    @FXML private Pane rootPane;
    @FXML private Pane darkness;
    @FXML private Pane titlePane;
    @FXML private GridPane contentPane;
    @FXML private GridPane GridPaneNickname;
    @FXML private GridPane nickNameContainer;

    // Botones
    @FXML private Button buttonModo;
    @FXML private Button buttonJugar;
    @FXML private Button buttonMusic;
    @FXML private Button buttonSalir;
    @FXML private Button buttonCreditos;
    @FXML private Button buttonPartidas;
    @FXML private Button btnNickCancelar;
    @FXML private Button btnNickConfirmar;
    @FXML private Button buttonInstrucciones;
    @FXML private Button buttonCambiarMusica;

    // ImageViews
    @FXML private ImageView fondoImage;

    // Labels
    @FXML private Label labelDigitos;
    @FXML private Label labelTitulo1;
    @FXML private Label labelTitulo2;

    // TextFields
    @FXML private TextField TextFieldNickname;

    // Stages
    private Stage stage;

    // Musica
    public static MediaPlayer musica;
    private static AudioClip sonidoMadera;
    private static AudioClip sonidoGaviota;
    public static AudioClip sonidoTeclado;

    // Variable para saber si el usuario tiene la música silenciada, o no
    public static Boolean desicionUsuario = true;

    // Arreglo del nombre de las canciones para poder recorrerlo y cambiarlas según decida el usuario
    public final static String musicas[] = {"Music1", "Music2", "Music3", "Music4", "Music5", "Music6", "Music7"};

    // Variable que almacena el índice actual para reproducir las canciones
    private int indiceActual = 0;

    // Metodo que se ejecuta al cargar la pantalla
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // Inicializando sonido
        sonidoMadera = new AudioClip(getClass().getResource("/Menu/Assets/woodHit.mp3").toString());
        sonidoGaviota = new AudioClip(getClass().getResource("/Menu/Assets/seagull.mp3").toString());
        sonidoTeclado = new AudioClip(getClass().getResource("/Menu/Assets/keyboard.wav").toString());

        // Inicializando música
        inicializarMusica();

        // Adapta la pantalla a su versión completa o en ventana
        javafx.application.Platform.runLater(() -> {
            Stage stage = (Stage) rootPane.getScene().getWindow();
            stage.setFullScreen(Menu.fullScreen);
        });

        // Deshabilitamos el botón de confirmar Nickname mientras éste sea menor a 3 caractéres y mayor a 10
        TextFieldNickname.textProperty().addListener((observable, oldValue, newValue) -> {
            btnNickConfirmar.setDisable(newValue.length() <= 3 || newValue.length() >= 10);
            labelDigitos.setVisible(newValue.length() <= 3 || newValue.length() >= 10);
        });

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

        // Adaptar los botones principales a la resolución del dispositivo
        buttonJugar.prefWidthProperty().bind(rootPane.widthProperty().divide(7));
        buttonJugar.prefHeightProperty().bind(rootPane.heightProperty().divide(10));

        // Adaptar el tamaño del botón para ir a los creditos a la resolución del dispositivo
        buttonCreditos.prefWidthProperty().bind(rootPane.widthProperty().divide(7));
        buttonCreditos.prefHeightProperty().bind(rootPane.heightProperty().divide(10));

        // Adaptar el tamaño del botón para ir a las instrucciones a la resolución del dispositivo
        buttonInstrucciones.prefWidthProperty().bind(rootPane.widthProperty().divide(6));
        buttonInstrucciones.prefHeightProperty().bind(rootPane.heightProperty().divide(10));

        // Adaptar el tamaño del botón para ir al registro de las partidas a la resolución del dispositivo
        buttonPartidas.prefWidthProperty().bind(rootPane.widthProperty().divide(7));
        buttonPartidas.prefHeightProperty().bind(rootPane.heightProperty().divide(10));

        // Adaptar el tamaño del botón para silenciar música a la resolución del dispositivo
        buttonMusic.prefWidthProperty().bind(rootPane.widthProperty().divide(12));
        buttonMusic.prefHeightProperty().bind(rootPane.heightProperty().divide(11));

        // Adaptar el tamaño del botón para cambiar la música a la resolución del dispositivo
        buttonCambiarMusica.prefWidthProperty().bind(rootPane.widthProperty().divide(12));
        buttonCambiarMusica.prefHeightProperty().bind(rootPane.heightProperty().divide(11));

        // Adaptar el tamaño del botón para cambiar entre pantalla completa y ventana a la resolución del dispositivo
        buttonModo.prefWidthProperty().bind(rootPane.widthProperty().divide(12));
        buttonModo.prefHeightProperty().bind(rootPane.heightProperty().divide(11));

        // Adaptar el tamaño del botón para salir a la resolución del dispositivo
        buttonSalir.prefWidthProperty().bind(rootPane.widthProperty().divide(12));
        buttonSalir.prefHeightProperty().bind(rootPane.heightProperty().divide(11));

        // Adaptar el tamaño del botón para cancelar el registro del Nickname del usuario a la resolución del dispositivo
        btnNickCancelar.prefWidthProperty().bind(rootPane.widthProperty().divide(17));
        btnNickCancelar.prefHeightProperty().bind(rootPane.heightProperty().divide(16));

        // Adaptar el tamaño del botón para confirmar el registro del Nickname del usuario a la resolución del dispositivo
        btnNickConfirmar.prefWidthProperty().bind(rootPane.widthProperty().divide(17));
        btnNickConfirmar.prefHeightProperty().bind(rootPane.heightProperty().divide(16));

        // Adaptar el panel que contiene el título del juego a la resolución del dispositivo
        titlePane.prefWidthProperty().bind(rootPane.widthProperty().divide(3));
        titlePane.prefHeightProperty().bind(rootPane.heightProperty().divide(1.1));

        // Adatpar el panel que contiene la segunda parte del título del juego a la resolución del dispositivo
        labelTitulo1.prefWidthProperty().bind(titlePane.widthProperty());
        labelTitulo1.prefHeightProperty().bind(titlePane.heightProperty());

        // Implementación del tamaño del a fuente del título del juego a los dos labels que lo componen

        Font fontTitulo = new Font(80);
        labelTitulo1.setFont(fontTitulo);
        labelTitulo2.setFont(fontTitulo);

        labelTitulo2.prefWidthProperty().bind(titlePane.widthProperty());
        labelTitulo2.prefHeightProperty().bind(titlePane.heightProperty());

        // Cargar el ícono del botón para salir
        Image imagenSalir = new Image(getClass().getResourceAsStream("/Menu/Assets/salir.png"));
        ImageView imageView = new ImageView(imagenSalir);
        imageView.setFitWidth(45);
        imageView.setFitHeight(45);
        buttonSalir.setGraphic(imageView);

        // Cargar el ícono del botón para silenciar o reproducir la música, este varía según la decisión

        if(desicionUsuario){ // En caso de tener la música reproduciéndose
            Image imagenMusica = new Image(getClass().getResourceAsStream("/Menu/Assets/musica.png"));
            ImageView imageView2 = new ImageView(imagenMusica);
            imageView2.setFitWidth(45);
            imageView2.setFitHeight(45);
            buttonMusic.setGraphic(imageView2);
        } else{ // En caso de tener la música silenciada
            Image imagenMusica = new Image(getClass().getResourceAsStream("/Menu/Assets/musicaMuteada.png"));
            ImageView imageView2 = new ImageView(imagenMusica);
            imageView2.setFitWidth(45);
            imageView2.setFitHeight(45);
            buttonMusic.setGraphic(imageView2);
        }

        // Cargar el ícono del botón cambiar la música reproduciéndose
        Image imagenFondo = new Image(getClass().getResourceAsStream("/Menu/Assets/cambiarMusica.png"));
        ImageView imageView3 = new ImageView(imagenFondo);
        imageView3.setFitWidth(45);
        imageView3.setFitHeight(45);
        buttonCambiarMusica.setGraphic(imageView3);

        // Cargar el ícono del botón para cambiar entre ventana y pantalla completa
        Image imagenModo = new Image(getClass().getResourceAsStream("/Menu/Assets/maximizar.png"));
        ImageView imageView4 = new ImageView(imagenModo);
        imageView4.setFitWidth(40);
        imageView4.setFitHeight(40);
        buttonModo.setGraphic(imageView4);

        // Cargar el ícono del botón para cancelar el registro del Nickname del usuario
        Image imagenCancelar = new Image(getClass().getResourceAsStream("/Menu/Assets/cancelar.png"));
        ImageView imageView5 = new ImageView(imagenCancelar);
        imageView5.setFitWidth(20);
        imageView5.setFitHeight(20);
        btnNickCancelar.setGraphic(imageView5);

        // Cargar el ícono del botón para confirmar el Nickname del usuario
        Image imagenOk = new Image(getClass().getResourceAsStream("/Menu/Assets/ok.png"));
        ImageView imageView6 = new ImageView(imagenOk);
        imageView6.setFitWidth(30);
        imageView6.setFitHeight(30);
        btnNickConfirmar.setGraphic(imageView6);
    }

    // Metodo para salir de la aplicación
    public void bottonSalir(ActionEvent e){
        System.exit(0);
    }

    // Metodo para cambiar entre pantalla completa y ventana
    public void bottonCambiarModo(ActionEvent e){
        stage = (Stage)((Node) e.getSource()).getScene().getWindow(); // Stage actual

        if(Menu.fullScreen){ // Si la aplicación DEBE estar en modo ventana
            stage.setFullScreen(false);
            stage.setMinHeight(720);
            stage.setMinWidth(1280);
            Menu.fullScreen = false; // Cambiar el estado de la variable estática "fullscreen" de la clase principal Menu

            // Crear un rectángulo del tamaño de la pantalla
            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();

            // Posicionar la ventana a la mitad del rectángulo anteriormente creado
            stage.setX((screenBounds.getWidth() - stage.getWidth()) / 2);
            stage.setY((screenBounds.getHeight() - stage.getHeight()) / 2);
        }
        else{
            stage.setFullScreen(true); // Establecer la ventana en modo pantalla completa
            Menu.fullScreen = true; // Cambiar el estado de la variable estática "fullscreen" de la clase principal Menu
        }
    }

    // Metodo para reproducir / silenciar música
    public void bottonMusica(ActionEvent e){
        musica.setMute(!musica.isMute()); // Cambia entre silencio y reproducción de la música

        if(desicionUsuario == true){ // Si el usuario silencia la música
            desicionUsuario = false; // Se cambia el estado de la variable estática "desicionUsuario" a falso

            // Se carga el nuevo ícono del botón para silenciar / reproducir música
            Image img = new Image(getClass().getResourceAsStream("/Menu/Assets/musicaMuteada.png"));
            ImageView imgV = new ImageView(img);
            imgV.setFitWidth(45);
            imgV.setFitHeight(45);
            buttonMusic.setGraphic(imgV); // Se asigna el nuevo ícono
        } else{ // Si el usuario reproduce la música
            desicionUsuario = true; // Se cambia el estado de la variable estática "desicionUsuario" a falso

            // Se carga el nuevo ícono del botón para silenciar / reproducir música
            Image img = new Image(getClass().getResourceAsStream("/Menu/Assets/musica.png"));
            ImageView imgV = new ImageView(img);
            imgV.setFitWidth(45);
            imgV.setFitHeight(45);
            buttonMusic.setGraphic(imgV); // Se asigna el nuevo ícono
        }
    }

    // Botón para ir a la pantalla de créditos
    public void cambiarCreditos(ActionEvent e) throws IOException {
        // Cargar el archivo FXML de la pantalla de créditos y la muestra

        Parent root = (Parent) FXMLLoader.load(getClass().getResource("/Creditos/Creditos.fxml"));
        Scene scene = rootPane.getScene();
        scene.getStylesheets().add(getClass().getResource("/Creditos/CreditosStyles.css").toExternalForm());
        stage = (Stage)((Node) e.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.hide();
        stage.setScene(scene);
        stage.show();
    }

    // botón para ir a la pantalla de instrucciones
    public void cambiarInstrucciones(ActionEvent e) throws IOException {
        // Cargar el archivo FXML de la pantalla de instrucciones y la muestra

        Parent nuevoRoot = FXMLLoader.load(getClass().getResource("/Instrucciones/Instrucciones.fxml"));
        Scene nuevaScene = new Scene(nuevoRoot);
        nuevaScene.getStylesheets().add(getClass().getResource("/Instrucciones/InstruccionesStyles.css").toExternalForm());
        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        stage.hide();
        stage.setScene(nuevaScene);
        stage.show();
    }

    // Botón que muestra el panel para ingresar el Nickname del usuario
    public void iniciarSesion(ActionEvent e){
        darkness.setVisible(true); // Mostrar el panel que simula la sombra detrás del menú
        nickNameContainer.setVisible(true); // Muestra el panel del ingreso del Nickname
        TextFieldNickname.clear(); // Limpia el TextField del Nickname del usuario
    }

    // Metodo que desactiva el panel para ingresar el Nickname del usuario
    public void cancelarInicioSesion(){
        darkness.setVisible(false); // Esconde el panel que simula la sombra
        nickNameContainer.setVisible(false); // Esconde el panel del ingreso del Nickname
    }

    // Metodo para ir a la pantalla de partidas registradas
    public void partidasRegistradas(ActionEvent e) throws IOException {
        // Cargar el archivo FXML de la pantalla de partidas registradas y la muestra

        Parent root = FXMLLoader.load(getClass().getResource("/Partidas/Partidas.fxml"));
        Scene scene = new Scene(root);

        scene.getStylesheets().add(getClass().getResource("/Partidas/PartidasStyles.css").toExternalForm());

        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        stage.hide();
        stage.setScene(scene);
        stage.show();
    }

    // Metodo para ir a la pantalla de la sala de espera
    public void cambiarSalaDeEspera(ActionEvent e) throws IOException {
        // Solo se intenta acceder para añadir el nickname si esta desde el menu
        if (this.TextFieldNickname != null) {
            if (!TextFieldNickname.getText().trim().isEmpty()) {
                Menu.nickName = TextFieldNickname.getText().trim();
            }
        }

        // Cargar el archivo FXML de la pantalla de la sala de espera y la muestra
        Parent nuevoRoot = FXMLLoader.load(getClass().getResource("/SalaDeEspera/SalaDeEspera.fxml"));
        Scene nuevaScene = new Scene(nuevoRoot);
        nuevaScene.getStylesheets().add(getClass().getResource("/SalaDeEspera/SalaDeEsperaStyles.css").toExternalForm());

        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        musica.setMute(!musica.isMute());
        stage.hide();
        stage.setScene(nuevaScene);
        stage.show();
    }

    // Metodos que reproduce el sonido ambiental al seleccionar una opción
    public void sonidoSeleccion(){
        sonidoMadera.setVolume(0.05);
        sonidoMadera.play();
    }

    // Metodo que reproduce un sonido ambiental al cambiar a la pantalla de la sala de espera
    public void sonidoGaviota(){
        sonidoGaviota.setVolume(0.2);
        sonidoGaviota.play();
    }

    // Metodo que reproduce un sonido de un teclado cada que se presiona una tecla en el Nickname
    public void sonidoTeclado(){
        sonidoTeclado.setVolume(0.1);
        sonidoTeclado.play();
    }

    //botton Pata Cambiar Canción
    public void ButtonSiguienteCancion() {
        if (musica != null) { // Si la música se está reproduciendo, la detiene
            musica.stop();
        }

        // avanza a la siguiente posición
        // toma el residuo para que sea "Circular"
        indiceActual = (indiceActual + 1) % musicas.length;

        // Reproducimos la nueva canción
        String nueva = musicas[indiceActual];
        Media media = new Media(getClass().getResource("/Menu/Assets/" + nueva + ".mp3").toString());
        musica = new MediaPlayer(media);
        musica.setCycleCount(MediaPlayer.INDEFINITE);
        musica.setVolume(0.2);

        //Si el usuario tiene la música muteada no va a sonar
        musica.setMute(!desicionUsuario);
        musica.play();
    }

    // Metodo que inicia la reproducción de la música
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