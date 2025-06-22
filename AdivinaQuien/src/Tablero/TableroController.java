package Tablero;

import Classes.Personaje;
import DataBaseClasses.PartidaDB;
import DataBaseClasses.PreguntasDB;
import Menu.Menu;
import Sockets.Cliente;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Consumer;
import Sockets.Servidor;
import TerminarPartida.TerminarPartidaController;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.effect.Glow;
import javafx.scene.effect.SepiaTone;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.media.AudioClip;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.util.Duration;
import Menu.MenuController;
import Sockets.Cliente.PersonajesListener;
import Sockets.Cliente.MensajeListener;

// Clase controlador de la pantalla del tablero (El juego como tal)

public class TableroController extends MenuController implements Initializable, PersonajesListener, MensajeListener {

    // Paneles
    @FXML private GridPane contentPane;
    @FXML private GridPane gridTable;
    @FXML private GridPane sideBarPane;
    @FXML private GridPane seleccionPersonaje;
    @FXML private GridPane gridPaneListaPer;
    @FXML private GridPane contenedorListaPreguntas;
    @FXML private GridPane gridPanePreguntas;
    @FXML private GridPane contenedorListaPer;
    @FXML private GridPane btnsContainer;
    @FXML private GridPane gridPaneRespuestas;
    @FXML private Pane shadowPanePreguntas;
    @FXML private Pane rootPane;
    @FXML private Pane shadowPanePersonajes;

    // ImageViews
    @FXML private ImageView fondoImage;
    @FXML private ImageView dadosImg;
    @FXML private ImageView userImg;
    @FXML private ImageView userRival;
    @FXML private ImageView listaImg;

    // Labels
    @FXML private Label labelPregunta;
    @FXML private Label labelJugador;
    @FXML private Label labelJugador2;
    @FXML private Label tiempoPartida;
    @FXML private Label labelFecha;

    // Botones
    @FXML private Button buttonEnviar;
    @FXML private Button btnLstaPreguntas;
    @FXML private Button btnRespNo;
    @FXML private Button btnRespSi;
    @FXML private Button buttonMusica;
    @FXML private Button buttonModo;
    @FXML private Button buttonSalir;
    @FXML private Button buttonSiguienteMusica;

    // TextFlows
    @FXML private TextFlow chat;

    // ScrollPanes
    @FXML private ScrollPane chatScroll;

    // TextFields
    @FXML private TextField textFieldMensaje;

    // Lista de imagenes para el tablero
    private final List<Image> imagenes = new ArrayList();

    // Atributo auxiliar para controlar la duración de la partida
    private long segundosTranscurridos = 0L;

    // Atributos para manejar los turnos
    private Cliente cliente; // Cliente para comunicarse con el servidor
    private boolean esMiTurno = false; // Booleano para indicar turnos
    private boolean preguntaEnviada = false; // Booleano para saber si ya se envio la pregunta
    private String miNickname = Menu.nickName; // Cadena para saber mi nickname
    private String nickNameOp = ""; // Cadena para saber el nickname del oponente

    private List<Personaje> personajesJuego; // Lista de personajes del tablero

    public int idPersonaje; // Id del personaje del usuario

    private int volteados = 0; // Contador de personajes volteados

    // Sonidos
    public static AudioClip sonidoVoltear;
    public static AudioClip sonidoMandar;
    public static AudioClip sonidoAdivinar;
    public static AudioClip sonidoBloqueado;
    public static AudioClip sonidoDados;
    public static AudioClip sonidoTablero;
    public static AudioClip sonidoLista;

    // Metodo que se ejecuta al cargar la escena
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        //Cargar los sonidos
        sonidoVoltear = new AudioClip(getClass().getResource("/Tablero/Assets/door.mp3").toString());
        sonidoMandar = new AudioClip(getClass().getResource("/Tablero/Assets/send.mp3").toString());
        sonidoAdivinar = new AudioClip(getClass().getResource("/Tablero/Assets/confirm.wav").toString());
        sonidoBloqueado = new AudioClip(getClass().getResource("/Tablero/Assets/blocked.wav").toString());
        sonidoDados = new AudioClip(getClass().getResource("/Tablero/Assets/dice.mp3").toString());
        sonidoTablero = new AudioClip(getClass().getResource("/Tablero/Assets/confirmTab.mp3").toString());
        sonidoLista = new AudioClip(getClass().getResource("/Tablero/Assets/paper.mp3").toString());

        inicializarMusica(); // Metodo para iniicar la música

        // Adapta la aplicación a su versión en pantalla completa o ventana
        Platform.runLater(() -> {
            Stage stage = (Stage)this.rootPane.getScene().getWindow();
            stage.setFullScreen(Menu.fullScreen);
        });

        LocalDate fechaActual = LocalDate.now(); // Variable que almacena la fecha actual

        DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy/MM/dd"); // Variable que formatea la fecha

        String fechaFormato = fechaActual.format(formato); // Variable con la fecha formateada

        labelFecha.setText(fechaFormato); // Se asigna la fecha al label correspondiente

        // Se le asignan clases de estilos iguales a los labels de la fecha y del nombre del jugador
        labelFecha.getStyleClass().add("labelTiempo");
        labelJugador.getStyleClass().add("labelTiempo");

        //Mandamos la fecha a base de datos
        Servidor.partida.setFecha(fechaActual);
        Cliente.partidaCliente.setFecha(fechaActual);

        this.sideBarPane.setVisible(false); // Se oculta el panel del chat, jugadores y preguntas

        this.labelJugador.setText(Menu.nickName); // Se asigna el nombre del jugador al label correspondiente

        // Se adapta la tamaño de la imagen de fondo a la resolución del dispositivo
        this.fondoImage.fitWidthProperty().bind(this.rootPane.widthProperty());
        this.fondoImage.fitHeightProperty().bind(this.rootPane.heightProperty());

        // Se adapta el tamaño del panel del contenido de la pantalla a la resolución del dispositivo
        this.contentPane.prefWidthProperty().bind(this.rootPane.widthProperty());
        this.contentPane.prefHeightProperty().bind(this.rootPane.heightProperty());

        // Se adapta el tamaño del panel contenedor de la lista de personajes a la resolución del dispositivo
        this.contenedorListaPer.prefWidthProperty().bind(this.contentPane.widthProperty());
        this.contenedorListaPer.prefHeightProperty().bind(this.contentPane.heightProperty());

        // Se adapta el tamaño del panel que simula sombra de la lista de personajes a la resolución del dispositivo
        this.shadowPanePersonajes.prefWidthProperty().bind(this.contentPane.widthProperty());
        this.shadowPanePersonajes.prefHeightProperty().bind(this.contentPane.heightProperty());

        // Se adapta el tamaño del contenedor de la lista de preguntas a la resolución del dispositivo
        this.contenedorListaPreguntas.prefWidthProperty().bind(this.contentPane.widthProperty());
        this.contenedorListaPreguntas.prefHeightProperty().bind(this.contentPane.heightProperty());

        // Se adapta el tamaño del panel que simula sombra de la lista de preguntas a la resolución del dispositivo
        this.shadowPanePreguntas.prefWidthProperty().bind(this.contentPane.widthProperty());
        this.shadowPanePreguntas.prefHeightProperty().bind(this.contentPane.heightProperty());

        // Se adapta el tamaño del gridPane que contiene el menú de respuesta a la pregunta entrante a la resolución del dispositivo
        this.gridPaneRespuestas.prefWidthProperty().bind(this.contentPane.widthProperty());
        this.gridPaneRespuestas.prefHeightProperty().bind(this.contentPane.heightProperty());

        // Se adapta el tamaño del contenedor de los botones de enviar mensaje y mostrar la lista de preguntas a la resolución del dispositivo
        this.btnsContainer.prefWidthProperty().bind(this.contentPane.widthProperty().multiply(.8));
        this.btnsContainer.prefHeightProperty().bind(this.contentPane.heightProperty().multiply(.5));

        // Se adapta el tamaño del textField para escribir un mensaje a la resolución del dispositivo
        this.textFieldMensaje.prefWidthProperty().bind(this.rootPane.widthProperty().multiply(.245));

        // Se adapta el tamaño del botón de enviar pregunta a la resolución del dispositivo
        this.buttonEnviar.prefWidthProperty().bind(rootPane.widthProperty().multiply(0.0525));
        this.buttonEnviar.prefHeightProperty().bind(rootPane.heightProperty().multiply(0.06));

        // Se adapta el tamaño del botón de mostrar la lista de preguntas a la resolución del dispositivo
        this.btnLstaPreguntas.prefWidthProperty().bind(rootPane.widthProperty().multiply(0.04));
        this.btnLstaPreguntas.prefHeightProperty().bind(rootPane.heightProperty().multiply(0.06));

        // Se adapta el tamaño del botón para repsonder con un "NO" a la resolución del dispositivo
        this.btnRespNo.prefWidthProperty().bind(rootPane.widthProperty().divide(10));
        this.btnRespNo.prefHeightProperty().bind(rootPane.heightProperty().divide(8));

        // Se adapta el tamaño del botón para responder con un "SI" a la resolución del dispositivo
        this.btnRespSi.prefWidthProperty().bind(rootPane.widthProperty().divide(10));
        this.btnRespSi.prefHeightProperty().bind(rootPane.heightProperty().divide(8));

        // Se adapta el tamaño de la imágen del personaje del usuario a la resolución del dispositivo
        this.userImg.fitWidthProperty().bind(this.rootPane.widthProperty().divide(6.5));
        this.userImg.fitHeightProperty().bind(this.rootPane.heightProperty().divide(4.5));

        // Se adapta el tamaño de la imágen del personaje del rival a la resolución del dispositivo
        this.userRival.fitWidthProperty().bind(this.rootPane.widthProperty().divide(6.5));
        this.userRival.fitHeightProperty().bind(this.rootPane.heightProperty().divide(4.5));

        // Se adapta el tamaño del ScrollPane del chat a la resolución del dispositivo
        this.chatScroll.prefWidthProperty().bind(rootPane.widthProperty().multiply(0.32));
        this.chatScroll.prefHeightProperty().bind(rootPane.heightProperty().multiply(0.52));

        // Se adapta el tamaño del chat a la resolución del dispositivo
        this.chat.prefWidthProperty().bind(rootPane.widthProperty().multiply(0.32));
        this.chat.prefHeightProperty().bind(rootPane.heightProperty().multiply(0.52));

        // Se adapta el tamaño de la imagen de la lista de personajes a la resolución del dispositivo
        this.listaImg.fitWidthProperty().bind(this.rootPane.widthProperty().divide(6.5));
        this.listaImg.fitHeightProperty().bind(this.rootPane.heightProperty().divide(4.5));
        // Se adapta el tamaño de la imagen de los dados a la resolución del dispositivo
        this.dadosImg.fitWidthProperty().bind(this.rootPane.widthProperty().divide(6));
        this.dadosImg.fitHeightProperty().bind(this.rootPane.heightProperty().divide(4));

        // El campo de mensaje y el boton para enviar el mensaje se encuentran deshabilitados
        // Hasta que se le asigne un turno o se reciba una pregunta
        textFieldMensaje.setDisable(true);
        buttonEnviar.setDisable(true);

        // Se vincula el metodo "imgMouseEntered" al evento OnMouseEntered de la imágen de los dados
        this.dadosImg.setOnMouseEntered(mouseEvent -> {imgMouseEntered(mouseEvent);});
        // Se vincula el metodo "imgMouseExited" al evento OnMouseExited de la imágen de los dados
        this.dadosImg.setOnMouseExited(mouseEvent -> {imgMouseExited(mouseEvent);});

        // Se vincula el metodo "imgMouseEntered" al evento OnMouseEntered de la imágen de la lista de personajes
        this.listaImg.setOnMouseEntered(mouseEvent -> {imgMouseEntered(mouseEvent);});
        // Se vincula el metodo "imgMouseExited" al evento OnMouseExited de la imágen de la lista de personajes
        this.listaImg.setOnMouseExited(mouseEvent -> {imgMouseExited(mouseEvent);});

        // Cargar el ícono del botón de enviar pregunta
        Image imagenEnviar = new Image(getClass().getResourceAsStream("/Tablero/Assets/mouseClick.png"));
        ImageView imageView = new ImageView(imagenEnviar);
        imageView.setFitWidth(35);
        imageView.setFitHeight(35);
        buttonEnviar.setGraphic(imageView);

        // Cargar el ícono del botón de la lista de preguntas
        Image imagenPregunta = new Image(getClass().getResourceAsStream("/Tablero/Assets/lista.png"));
        ImageView imageView1 = new ImageView(imagenPregunta);
        imageView1.setFitWidth(35);
        imageView1.setFitHeight(35);
        btnLstaPreguntas.setGraphic(imageView1);

        // Cargar el ícono del botón para cambiar entre pantalla completa y ventana
        Image imagenModo= new Image(getClass().getResourceAsStream("/Menu/Assets/maximizar.png"));
        ImageView imageView2 = new ImageView(imagenModo);
        imageView2.setFitWidth(40);
        imageView2.setFitHeight(40);
        buttonModo.setGraphic(imageView2);

        // Cargar el ícono del botón para salir de la lista de personajes a elegir
        Image imagenSalir = new Image(getClass().getResourceAsStream("/Tablero/Assets/salir.png"));
        ImageView imageView3 = new ImageView(imagenSalir);
        imageView3.setFitWidth(35);
        imageView3.setFitHeight(35);
        buttonSalir.setGraphic(imageView3);

        // Cargar el ícono del botón para cambiar la música
        Image imagenFondo = new Image(getClass().getResourceAsStream("/Menu/Assets/cambiarMusica.png"));
        ImageView imageView4 = new ImageView(imagenFondo);
        imageView4.setFitWidth(45);
        imageView4.setFitHeight(45);
        buttonSiguienteMusica.setGraphic(imageView4);

        //Dependiendo de la decisión del usuario en el menú sobre la música, cargamos el botón con el icono correspondente
        if(desicionUsuario == false){
            Image img = new Image(getClass().getResourceAsStream("/Menu/Assets/musicaMuteada.png"));
            ImageView imgV = new ImageView(img);
            imgV.setFitWidth(40);
            imgV.setFitHeight(40);
            buttonMusica.setGraphic(imgV);
        }
        else{
            Image img = new Image(getClass().getResourceAsStream("/Menu/Assets/musica.png"));
            ImageView imgV = new ImageView(img);
            imgV.setFitWidth(40);
            imgV.setFitHeight(40);
            buttonMusica.setGraphic(imgV);
        }
    }

    // Metodo para asignar el oponente al usuario actual
    public void setOponente(String oponente){
        this.nickNameOp = oponente; // Almacenar el Nickname del oponente
        this.labelJugador2.setText(oponente); // Asignar el Nickname del oponente al label correspondiente

        // Mandamos el nickname del oponente a la base de datos
        Cliente.partidaCliente.setJugador2(this.nickNameOp);
    }

    // Metodo que muestra la lista de preguntas
    public void listaPreguntas(){
        this.shadowPanePreguntas.setVisible(true); // Mostrar el panel que simula la sombra de la lista de preguntas
        this.contenedorListaPreguntas.setVisible(true); // Mostrar el contenedor de la lista de preguntas
        cargarListaPreguntas(); // Metodo para cargar las preguntas en un ListView
    }

    // Metodo para cerrar el panel de la lista de preguntas
    public void salirPreguntas(){
        this.shadowPanePreguntas.setVisible(false); // Ocultar el panel que simula la sombra de la lista de preguntas
        this.contenedorListaPreguntas.setVisible(false); // Ocultar el contenedor de la lista de preguntas
    }

    // Metodo para cargar las preguntas al ListView
    private void cargarListaPreguntas() {
        // Se pide la lista de preguntas a la base de datos
        List<String> preguntasDesdeDB = PreguntasDB.obtenerPreguntas();
        // Se adapta la lista a un objeto de tipo ObservableList
        ObservableList<String> preguntasObservables = FXCollections.observableArrayList(preguntasDesdeDB);

        // Se crea la instancia del ListView que mostrará las preguntas, asignándole la lista observable anteriormente creada
        ListView<String> listViewPreguntas = new ListView<>(preguntasObservables);

        // Se le asigna una clase para sus estilos
        listViewPreguntas.getStyleClass().add("listView");
        listViewPreguntas.setOrientation(Orientation.VERTICAL); // Se le asigna una orientación vertical

        // Se crea un consumidor que definirá qué pasa cuando se selecciona una de las preguntas
        Consumer<String> accionSeleccionarPregunta = pregunta -> { // Al presionar una pregunta
            textFieldMensaje.setText(pregunta); // Se añade esa pregunta al TextField del chat
            salirPreguntas(); // Se cierra el menú de las preguntas
        };

        // Se cargan todos los índices de la lista con las preguntas y un botón que dice "Preguntar"
        listViewPreguntas.setCellFactory(param -> new ListCell<String>() {
            private final BorderPane layout = new BorderPane(); // Se crea un panel para almacenar la pregunta y el botón
            private final Label labelPregunta = new Label(); // Se crea el Label para la pregunta
            private final Button botonPreguntar = new Button("Preguntar"); // Se crea el botón para preguntar

            { // Se crea el Layout para el panel de la pregunta y el botón
                layout.setLeft(labelPregunta); // Se asigna el label de la pregunta al lado izquierdo del panel
                layout.setRight(botonPreguntar); // Se asigna el botón al lado derecho del panel

                // Se asigna un márgen al panel de 10 pixeles hacia la derecha para separar la pregunta y el botón
                BorderPane.setMargin(labelPregunta, new Insets(0, 10, 0, 0));

                // Se le asigna un contenido al evento SetOnAction al botón
                botonPreguntar.setOnAction(event -> { // Al presionar el botón
                    if (getItem() != null && !getItem().isEmpty()) {
                        /*
                           Se llama al consumidor anteriormente creado, el cual pondrá la pregunta en el chat y
                           cerrará el menú de preguntas
                        */
                        accionSeleccionarPregunta.accept(getItem());
                    }
                });
            }

            // Metodo que agrega la pregunta y el botón al ListView
            @Override
            protected void updateItem(String pregunta, boolean empty) {
                super.updateItem(pregunta, empty); // Se lee cada pregunta

                if (empty || pregunta == null) { // Si se recibe una pregunta vacía, o un null del metodo updateItem del padre
                    setGraphic(null); // No se crea ningún índice de la lita
                } else { // En caso de recibir algo
                    labelPregunta.setText(pregunta); // Se le asigna el texto de la pregunta al label
                    labelPregunta.setWrapText(true); // Se adapta el texto al label
                    setGraphic(layout); // Se inserta el layout del panel que contiene el label y el botón al ListView
                }
            }
        });

        gridPanePreguntas.add(listViewPreguntas, 0, 1); // Se añade el ListView al gridPane contenedor de la lista de preguntas
    }

    // Metodo que carga las imagenes de los personajes para mostrarlos en el ImageView de selección de personaje
    private void cargarListaPersonajes() {
        // Se crea una lista observable a partir de la lista de personajes que es parámetro de la clase
        ObservableList<Personaje> personajes = FXCollections.observableArrayList(this.personajesJuego);

        // Se crea el ListView a mostrar con la lista observable anterior
        ListView<Personaje> listViewPersonajes = new ListView((ObservableList)personajes);
        // Se le asigna una orientación vertical
        listViewPersonajes.setOrientation(Orientation.VERTICAL);

        // Se cargan todos los índices de la lista con una imágen, un label y su contenedor
        listViewPersonajes.setCellFactory(param -> new ListCell<Personaje>(){
            private final ImageView imageView = new ImageView(); // Se crea la instancia del ImageView de la imágen
            private final Label label = new Label(); // Se crea el label para el nombre del personaje
            private final VBox layout = new VBox(10, imageView, label); // Se crea su contenedor, asignando la imagen y el label

            // Metodo que agrega los contenedores al ListView
            @Override
            protected void updateItem(Personaje personaje, boolean empty){
                super.updateItem(personaje, empty); // Se lee cada personaje

                if(empty || personaje == null){ // En caso de recibir algún dato nulo
                    // No se asigna nada al ImageView
                    setText(null);
                    setGraphic(null);
                } else { // En caso de recibir algo
                    Image img = personaje.getImagenFX(); // Se recupera la imagen del personaje actual iterado
                    imageView.setImage(img); // Se le asigna la imagen al ImageView anteriormente creado
                    // Se configura la imagen para estilizarla
                    imageView.setFitWidth(100);
                    imageView.setPreserveRatio(true);

                    label.setText(personaje.getNombre()); // Se asigna el nombre del personaje actual iterado al label
                    layout.setAlignment(Pos.CENTER); // Se alinean todos los objetos en el centro
                    setGraphic(layout); // Se asigna el contenedor al índice actual de la lista
                }
            }
        });

        // Se le asigna un modelo para cuando se selecciona algún índice
        listViewPersonajes.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) { // Si existe alguna selección
                Image img = newSelection.getImagenFX(); // Se obtiene la imágen seleccionada

                this.userImg.setImage(img); // Se le asigna la imagen seleccionada a la imagen del usuario en el tablero

                // Se almacena el índice de la imagen seleccionada
                this.idPersonaje = newSelection.getIdTablero() + 1;

                cerrarListaPersonajes(null); // Se cierra la lista de personajes

                this.seleccionPersonaje.setVisible(false); // Se oculta el panel de selección de personajes
                contenedorListaPer.setVisible(false); // Se oculta el contenedor de la lista de personajes
                shadowPanePersonajes.setVisible(false); // Se oculta el panel que simula la sombra de la lista de personajes

                this.sideBarPane.setVisible(true); // Se muestra el panel de jugadores, chat y  lista de preguntas

                reasignarMetodos(); // Se reasignan los metodos de las imagenes del tablero

                // Determinamos el Id y llamamos al metodo de personaje elegido
                int idPersonajeSelec = newSelection.getIdTablero() + 1;
                personajeElegido(idPersonajeSelec);
            }
        });

        // Se agrega la lista de personajes a la pantalla 
        gridPaneListaPer.add(listViewPersonajes, 0, 1);
    }

    // Metodo para avisar al servidorque se ejigio personaje
    private void personajeElegido(int idPersonajeSelec) {
        //Guardamos el Id del personaje
        this.idPersonaje = idPersonajeSelec;

        // Avisamos al servidor que el personaje se ha elegido
        cliente.enviarMensaje("PERSONAJE_ELEGIDO", String.valueOf(idPersonajeSelec));
        agregarMensajeAlChat("Sistema", "Has elegido tu personaje. Esperando a tu oponente ;)");

        // Deshabilita la selección de personaje y habilita la interfaz de juego
        this.seleccionPersonaje.setVisible(false);
        this.sideBarPane.setVisible(true);
    }

    // Metodo que entra en la fase de elección de personaje del jugador
    private void elegirPersonaje(){
        this.seleccionPersonaje.setVisible(true); // Se hace visible el panel de selección de personaje
    }

    // Metodo que se ejecuta cuando se elige un personaje del tablero
    public void eleccionTablero(MouseEvent e){
        StackPane stack = (StackPane) e.getSource(); // Obtenemos el panel al que se dió click

        // Se reproduce un sonido al dar click
        sonidoTablero.setVolume(0.2);
        sonidoTablero.play();

        // Como en el tablero, las imagenes estan por debajo de un "StackPane", debemos iterar sobre este para llegar a su hijo que es la imagen
        for(Node hijo: stack.getChildren()){
            if(hijo instanceof ImageView){ // Al encontrar el hijo del StackPane que es la imagen
                hijo.setEffect(null); // Se le quita cualquier efecto que pueda tener
                Image img = ((ImageView)hijo).getImage(); // Se crea una instancia de una nueva imagen igual a la que se dio click
                this.userImg.setImage(img); // Se le asigna al usuario esa misma imagen
                break;
            }
        }

        this.seleccionPersonaje.setVisible(false); // Se oculta el panel de selección de personaje
        this.sideBarPane.setVisible(true); // Se muestra el panel del chat, los jugadores y las preguntas

        reasignarMetodos(); // Se reasignan los métodos

        this.idPersonaje = gridTable.getChildren().indexOf(stack); // Se almacena el id del personaje seleccionado

        personajeElegido(this.idPersonaje); // Metodo que indica al servidor que un personaje se eligió
    }

    // Metodo que se ejecuta cuando se da click en la imagen de los dados (Personaje aleatorio)
    public void eleccionRandom(){
        Random random = new Random(); // Se crea una instancia de la clase Random

        // Se reproduce un sonido
        sonidoDados.setVolume(0.2);
        sonidoDados.play();

        this.dadosImg.setEffect(null); // Se le quita cualquier efecto que pueda tener a la imagen de los dados

        // Se le quitan los eventos aplicados a la imagen de los dados
        this.dadosImg.setOnMouseEntered(null);
        this.dadosImg.setOnMouseExited(null);

        int rand = random.nextInt(24); // Se crea un nuevo numero random entre el 0 y el 23

        // Se obtiene el StackPane que se encuentra en el índice del número random generado
        StackPane stackPane = (StackPane)this.gridTable.getChildren().get(rand);
        // Se obtiene la imagen que se encuentra en aquel StackPane
        ImageView imgView = (ImageView)stackPane.getChildren().get(0);

        Image img = imgView.getImage();
        this.userImg.setImage(img); // Esta imagen obtenida se le asigna al usuario

        this.seleccionPersonaje.setVisible(false); // Se oculta el panel de selección de personaje
        this.sideBarPane.setVisible(true); // Se hace visible el panel del chat, usuarios y preguntas

        reasignarMetodos(); // Se reasignan los metodos a los personajes dle tablero

        this.idPersonaje = rand; // Se almacena el id del personaje seleccionado

        personajeElegido(rand); // Se informa al servidor que se eligió personaje
    }

    // Metodo que se ejecuta cuando se elige un personaje de la lista de personajes
    public void eleccionLista(){
        // Se reproduce un sonido
        sonidoLista.setVolume(0.2);
        sonidoLista.play();

        contenedorListaPer.setVisible(true); // Se muestra el contenedor de la lista de personajes
        shadowPanePersonajes.setVisible(true); // Se muestra el panel que simula la sombra de la lista de personajes
    }

    // Metodo para cerrar la lista de personajes
    public void cerrarListaPersonajes(ActionEvent e){
        contenedorListaPer.setVisible(false); // Oculta el contenedor de la lista de personajes
        shadowPanePersonajes.setVisible(false); // Oculta el panel que simula la sombra de la lista de personajes
    }

    // Metodo que reasigna los metodos a los StackPane del tablero
    private void reasignarMetodos(){
        for(int i=1; i<24; i++) { // Ciclo para recorrer cada celda del tablero
            this.gridTable.getChildren().get(i).setOnMouseClicked(null); // Se quita el evento OnMouseClicked
            // Se vinbula el evento OnMouseEntered con el metodo MouseEntro
            this.gridTable.getChildren().get(i).setOnMouseEntered(mouseEvent -> {mouseEntro(mouseEvent);});
            // Se vinbula el evento OnMouseExited con el metodo MouseSalio
            this.gridTable.getChildren().get(i).setOnMouseExited(mouseEvent -> {mouseSalio(mouseEvent);});
        }
    }

    // Metodo que se ejecuta cuando el mouse entra a cualquier StackPane del tablero
    private void mouseEntro(MouseEvent e){
        StackPane stack = (StackPane)e.getSource(); // Se obtiene el StackPane en el que se encuentra el mouse

        if(stack.getChildren().getFirst() instanceof ImageView) { // Si el StackPane tiene como hijo un ImageView
            // Se crea una instancia de un Image que almacena la imagen dentro del StackPane
            Image img = ((ImageView) stack.getChildren().getFirst()).getImage();
            int indice = imagenes.indexOf(img); // Se busca el índice en el cual se encuentra aquella imagen

            int row = 0, col = 0;

            // Se convierte aquel índice a coordenadas dentro del tablero
            if (indice > 5) { // Si el índice es mayor al total de columnas del tablero
                row = indice / 6; // El renglón será el índice entre 6
                col = indice % 6; // La columna será el renglon módulo 6
            } else { // De lo contrario, será la primera fila, y la columna será igual al índice
                row = 0;
                col = indice;
            }

            // Se crea una nueva instancia de un BorderPane
            BorderPane menuPersonajeContenedor = new BorderPane();
            // Se crea una nueva instancia de un GridPane
            GridPane menuPersonaje = new GridPane();

            menuPersonaje.setAlignment(Pos.CENTER); // se centra el ontenido del gridPane
            menuPersonaje.setHgap(4); // Se asigna una separación de 4 pixeles entre columnas

            // Se cargan los íconos de adivinar y de voltear la tarjeta
            Image iconAdivinar = new Image(getClass().getResourceAsStream("/Tablero/Assets/adivinar.png"));
            Image iconVoltear = new Image(getClass().getResourceAsStream("/Tablero/Assets/voltear.png"));

            // Se crean los ImageViews para los íconos
            ImageView iconAdivinarIV = new ImageView(iconAdivinar);
            ImageView iconVoltearIV = new ImageView(iconVoltear);

            // Se redimensionan los íconos
            iconAdivinarIV.setFitWidth(24);
            iconAdivinarIV.setFitHeight(24);

            iconVoltearIV.setFitWidth(24);
            iconVoltearIV.setFitHeight(24);

            // Se crea la instancia del botón de voltear
            Button botonVoltear = new Button();
            botonVoltear.setMaxWidth(Double.MAX_VALUE);
            botonVoltear.setGraphic(iconVoltearIV); // Se le asigna el ícono de voltear

            // Se crea el botón de adivinar
            Button botonAdivinar = new Button();
            botonVoltear.setMaxWidth(Double.MAX_VALUE);
            botonAdivinar.setGraphic(iconAdivinarIV); // Se le asigna el ícono de adivinar

            // Se adapta el tamaño de los botones a la resolución del dispositivo
            botonVoltear.prefWidthProperty().bind(rootPane.widthProperty().divide(35));
            botonVoltear.prefHeightProperty().bind(rootPane.heightProperty().divide(25));
            botonAdivinar.prefWidthProperty().bind(rootPane.widthProperty().divide(35));
            botonAdivinar.prefHeightProperty().bind(rootPane.heightProperty().divide(25));

            // Se crea un nuevo efecto de sombra
            Effect sombra = new DropShadow();

            // Se le asigna el efecto a los dos botones
            botonAdivinar.setEffect(sombra);
            botonVoltear.setEffect(sombra);

            // Se le asigna una acción al evento OnMouseClicked del botón de adivinar
            botonAdivinar.setOnMouseClicked(mouseEvent -> { // Si se presiona el botón
                try {
                    adivinar(mouseEvent, indice); // Metodo que indica que se adivinó el personaje con tal índice
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            });

            // Se le asigna una acción al evento OnMouseClicked del botón de voltear
            botonVoltear.setOnMouseClicked(mouseEvent -> { // Si se presiona el botón
                try {
                    voltear(mouseEvent, indice); // Metodo para voltear la tarjeta con tal indice
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            });

            if(personajesJuego.get(indice).isTachado()){ // En caso de que el personaje ya este tachado
                botonAdivinar.setDisable(true); // Se deshabilida el botón de adivinar
            } else{ // De lo contrario
                botonAdivinar.setDisable(false); // Se habilita el botón de adivinar
            }

            menuPersonaje.add(botonVoltear, 0, 0); // Se añade al gridPane el botón de voltear
            menuPersonaje.add(botonAdivinar, 1, 0); // Se añade al gripane el botón de adivinar
            menuPersonajeContenedor.setBottom(menuPersonaje); // Se añade el gridPane a la parte baja del borderPane

            stack.getChildren().add(menuPersonajeContenedor); // Se añade el borderPane al stack que esta debajo del mouse
        }
    }

    // Metodo para detectar que el mouse salió del personaje
    private void mouseSalio(MouseEvent e){
        StackPane stack = (StackPane)e.getSource(); // Se obtiene el stackPane donde se encuentra el personaje

        for (Node hijo : stack.getChildren()) { // Recorremos los hijos del stackPane
            if (hijo instanceof BorderPane) { // Si el hijo actual es un BorderPane
                stack.getChildren().remove(hijo); // Se elimina aquel hijo, dejando la pura imágen
                break;
            }
        }
    }

    // Metodo para voltear las tarjetas de los personajes
    private void voltear(MouseEvent e, int indice) throws IOException {
        // Se obtiene el StackPane del índice recibido como parámetro
        StackPane stack = (StackPane)gridTable.getChildren().get(indice+1);

        for (Node hijo : stack.getChildren()) { // Se recorren los hijos del stackPane
            if (hijo instanceof ImageView) { // Si el hijo es un ImageView
                if(personajesJuego.get(indice).isTachado()){ // Si el personaje actual ya está tachado
                    hijo.setEffect(null); // Se le quita el efecto
                    personajesJuego.get(indice).setTachado(false); // Se le quita lo tachado
                    this.volteados--; // Se disminuye el conteo de personajes volteados en el tablero
                } else{ // Si no esta tachado aún
                    Effect efecto = new SepiaTone(); // Se crea un efecto sepia
                    hijo.setEffect(efecto); // Se le aplica el efecto a la imagen del personaje

                    personajesJuego.get(indice).setTachado(true); // Se tacha el personaje
                    this.volteados++; // Se aumenta el conteo de personajes volteados en el tablero;

                    if(this.volteados == 24){ // Si ya se voltearon todos los personajes
                        TerminarPartidaController.estado = false; // Se asigna la variable de estado global como que perdió el usuario

                        // Se carga el archivo FXML de la pantalla de ganar / perder y la muestra
                        Parent root = FXMLLoader.load(getClass().getResource("/TerminarPartida/TerminarPartida.fxml"));
                        Scene scene = new Scene(root);
                        scene.getStylesheets().add(getClass().getResource("/TerminarPartida/TerminarPartidaStyles.css").toExternalForm());
                        Stage stage = new Stage();
                        stage = (Stage)((Node) e.getSource()).getScene().getWindow();
                        stage.hide();
                        stage.setScene(scene);
                        stage.show();
                    }
                }
                continue;
            }

            if (hijo instanceof BorderPane){ // Si el hijo es un BorderPane
                // Se obtiene el GridPane de la parte inferior del BorderPane
                GridPane grid = (GridPane)((BorderPane) hijo).getBottom();

                // Si el personaje de aquel índice ya esta tachado, se deshabilita el botón de adivinar
                if(personajesJuego.get(indice).isTachado()) grid.getChildren().get(1).setDisable(true);
                else grid.getChildren().get(1).setDisable(true); // Si no, se habilita

                // Se reproduce un sonido
                sonidoVoltear.setVolume(0.2);
                sonidoVoltear.play();
                continue;
            }
        }

        // Mandamos los segundos a la base de datos
        Servidor.partida.setTiempo(java.time.Duration.ofSeconds(segundosTranscurridos));
        Cliente.partidaCliente.setTiempo(java.time.Duration.ofSeconds(segundosTranscurridos));

        // Se inserta la partida en la base de datos
        PartidaDB.insertarPartida(Servidor.partida);
        PartidaDB.insertarPartida(Cliente.partidaCliente);
    }

    // Metodo para adivinar el personaje
    private void adivinar(MouseEvent e, int indice) throws IOException {
        // Se reproduce un sonido
        sonidoAdivinar.setVolume(0.2);
        sonidoAdivinar.play();

        // Obtenemos el indice que selecciono para adivinar
        int idAdivinar = indice + 1;

        // Le decimos al servidor que el usuario adivino personaje
        cliente.enviarMensaje("ADIVINAR", String.valueOf(idAdivinar));

        // PARA LA REDIRECCION LA MANEJO DESDE EL SERVIDOR PARA EL ID Y SI ES O NO


        /*Parent root = FXMLLoader.load(getClass().getResource("/TerminarPartida/TerminarPartida.fxml"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/TerminarPartida/TerminarPartidaStyles.css").toExternalForm());
        Stage stage = new Stage();
        stage = (Stage)((Node) e.getSource()).getScene().getWindow();
        stage.hide();
        stage.setScene(scene);
        stage.show();*/
    }

    // Metodo para asignar el cliente del servidor
    public void setCliente(Cliente cliente) {
        this.cliente = cliente;

        // Reiniciar estados del turno y pregunta para una nueva partida
        this.esMiTurno = false;
        this.preguntaEnviada = false;

        // Configuramos listeners
        this.cliente.setMensajeListener(this::onManejarMensajeServidor);
        this.cliente.setPersonajeListener(this::onPersonajesRecibidos);
        System.out.println("TableroController: Cliente asignado y listeners configurados");
    }

    // Implementación de los Listeners
    public void onPersonajesRecibidos(List<Personaje> personajes) {
        Platform.runLater(() -> {
            System.out.println("TableroController: Lista de personajes recibida");
            this.personajesJuego = personajes; // Guardamos la lista

            // Limpiamos completamente la rejilla y la lista de imágenes de la partida anterior.
            gridTable.getChildren().clear();
            imagenes.clear();
            chat.getChildren().clear();
            this.segundosTranscurridos = 0L;

            // Reiniciar los estados del turno y pregunta para la NUEVA partida.
            this.esMiTurno = false;
            this.preguntaEnviada = false;

            // Mostramos los personajes en el tablero
            cargarListaImagenes();
            mostrarPersonajesEnTablero();
            cargarListaPersonajes();

            // Deshabilitar el chat inicialmente (será habilitado cuando se reciba el TU_TURNO)
            textFieldMensaje.setDisable(true);
            buttonEnviar.setDisable(true);
            textFieldMensaje.clear(); // Limpiar cualquier texto viejo

            // Movi la seleccion de personajes despues de cargar todo
            System.out.println("\nLLAMANDO A ELEGIR PERSONAJE");
            elegirPersonaje();
        });
    }

    // Metodo para almacenar las imagenes de cada personaje en una lista de tipo Image
    private void cargarListaImagenes(){
        for(int i=0; i<(gridTable.getRowCount()*gridTable.getColumnCount()); i++){
            this.imagenes.add(personajesJuego.get(i).getImagenFX());
        }
    }

    // Metodo para mostrar los personajes en el tablero
    public void mostrarPersonajesEnTablero(){
        int indice = 0; // Indice de la lista de imagenes

        for(int i = 0; i < 4; i++) {
            for(int j = 0; j < 6; j++) {
                Image img = (Image)this.imagenes.get(indice); // Se crea una instancia de una Image
                ImageView imageView = new ImageView(img); // Se le asigna a un ImageView

                // Se adapta el tamaño de la imagen a la celda del tablero
                imageView.fitWidthProperty().bind(this.gridTable.widthProperty().divide(6).subtract(4));
                imageView.fitHeightProperty().bind(this.gridTable.heightProperty().divide(4).subtract(4));
                imageView.setPreserveRatio(false);

                // Se crea un nuevo StackPane
                StackPane stack = new StackPane();
                stack.getChildren().add(imageView); // Se le añade el ImageView al stackPane

                // Se le asignan los eventos necesarios al stackPane (MouseEntered, MouseExited y MouseClicked)
                stack.setOnMouseClicked(mouseEvent -> {eleccionTablero(mouseEvent);});
                stack.setOnMouseEntered(mouseEvent -> {imgMouseEntered(mouseEvent);});
                stack.setOnMouseExited(mouseEvent -> {imgMouseExited(mouseEvent);});

                this.gridTable.add(stack, j, i); // Se añade el stackPane al tablero
                indice++; // Se aumenta el índice actual de la lista de imagenes
            }
        }
    }

    // Metodo para determinar si el mouse entró a la imagen en la selección de personaje
    private void imgMouseEntered(MouseEvent e){
        /*
        * Si el mouse está encima de un ImageView, significa que está colocado sobre la imagen de los dados
        * o de la lista. En caso contrario, si está encima de un StackPane, significa que está colocado sobre
        * algún personaje del tablero.
        */

        if(e.getSource() instanceof ImageView){ // Si está encima de un ImageView
            Effect effect = new Glow(0.6); // Se crea un efecto de brillo
            ImageView imageView = (ImageView)e.getSource(); // Se obtiene el ImageView en el que está el mouse
            imageView.setEffect(effect); // Se le aplica el efecto al ImageView
        } else if(e.getSource() instanceof StackPane){ // En caso de estar sobre un Stackpane
            StackPane stack = (StackPane) e.getSource(); // Se obtiene el StackPane en el que está el mouse

            for(Node hijo : stack.getChildren()){ // Se recorren los hijos del StackPane
                if(hijo instanceof ImageView){ // Si el hijo actual es un ImageView
                    Effect effect = new Glow(0.6); // Se crea un nuevo efecto de brillo
                    hijo.setEffect(effect); // Se le aplica el efecto al hijo
                }
            }
        }
    }

    // Metodo para determinar si el mouse salió de la imagen en la selección de personaje
    private void imgMouseExited(MouseEvent e){
         /*
         * Si el mouse está encima de un ImageView, significa que está colocado sobre la imagen de los dados
         * o de la lista. En caso contrario, si está encima de un StackPane, significa que está colocado sobre
         * algún personaje del tablero.
         *
         */

        if(e.getSource() instanceof ImageView){ // Si esta encima de un ImageView
            ImageView imageView = (ImageView)e.getSource(); // Se obtiene el ImageView en el que está el mouse
            imageView.setEffect(null); // Se le quita cualquier efecto que tuviera el ImageView
        } else if(e.getSource() instanceof StackPane){ // Si el mouse está encima de un StackPane
            StackPane stack = (StackPane) e.getSource(); // Se obtiene el StackPane en el que se encuentra el mouse

            for(Node hijo : stack.getChildren()){ // Se recorren los hijos del StackPane
                if(hijo instanceof ImageView){ // Si el hijo es un ImageView
                    hijo.setEffect(null); // Se le quita cualquier efecto que tuviera el ImageView
                }
            }
        }
    }

    // Metodo para enviar preguntas al oponente
    public void enviarMensaje(ActionEvent e) {
        String mensaje = textFieldMensaje.getText().trim();
        if (mensaje.isEmpty()) {
            return; // Esto para no enviar mensajes vacios
        }

        // Si es tu turno y no se ha enviado la pregunta inicial
        if (esMiTurno && !preguntaEnviada) {
            // Enviamos la pregunta al servidor
            cliente.enviarMensaje("PREGUNTA", mensaje);

            // Muestro mi pregunta en mi chat
            agregarMensajeAlChat(miNickname, mensaje);

            preguntaEnviada = true; // Indicamos que ya envio la pregunta
            // Deshabilitamos el chat y el boton hasta que se reciba una respuesta
            textFieldMensaje.setDisable(true);
            buttonEnviar.setDisable(true);
            textFieldMensaje.clear();
            System.out.println("Pregunta enviada: " + mensaje);
            // Si no es mi turno, y la pregunta fue enviada para responder y esta respondiendo si o no
        } else if (!esMiTurno && !textFieldMensaje.isDisable() && (mensaje.equalsIgnoreCase("SI") || mensaje.equalsIgnoreCase("NO"))) {
            /* Entonces la variable "preguntaEnviada" en el cliente que RESPONDE va a ser "true"
             * pero solo cuando el se recibe la pregunta y se habilitara el chat para que responda si o no*/

            cliente.enviarMensaje("RESPUESTA", mensaje);

            // Mostramos en el chat locar el mensaje
            agregarMensajeAlChat(miNickname,mensaje);

            textFieldMensaje.setDisable(true); // Deshabilitamos el chat despues de responder
            buttonEnviar.setDisable(true);
            textFieldMensaje.clear();
            System.out.println("Respuesta enviada: " + mensaje);
            // Si no es ningun caso, entonces aparecera que no se puede enviiar mensaje
        } else {
            agregarMensajeAlChat("Sistema", "No puedes enviar mensaje en este momento");
            textFieldMensaje.clear();
        }
    }

    // Metoddo para agregar los mensajes al TextFlow del chat
    private void agregarMensajeAlChat(String nick, String mensaje) {
        Platform.runLater(() -> {
            // Se crean dos textos diferentes para que el primero, el que contiene el nombre del jugador, esté en negritas y resalte más
            Text nickText = new Text(nick + ": "); // Se crea un nuevo texto con el nickName del jugador y un ":"
            nickText.setFont(Font.font("System", FontWeight.BOLD, 22)); // Se le pone la fuente deseada

            Text mensajeText = new Text(mensaje + "\n"); // Se crea otro texto con el mensaje y un salto de línea
            mensajeText.setFont(Font.font("System", FontWeight.NORMAL, 20)); // Se le pone la fuente deseada

            chat.getChildren().addAll(nickText, mensajeText); // Se añaden los dos textos creados
        });
    }

    @Override
    // Metodo que recibira los mensajes del servidor
    public void onManejarMensajeServidor(String mensaje) {
        Platform.runLater(() -> {
            // Vemos si el mensaje empieza por "TU TURNO"
            if (mensaje.startsWith("TU_TURNO:")) {

                // Si es asi asignamos a una cadena el nickname "TU TURNO: nickname del jugador con el turno"
                String nickTurno = mensaje.substring("TU_TURNO:".length()).trim();
                // Lo comparamos con el miNickname para ver si son el mismo
                if (nickTurno.equals(miNickname)) {
                    // Si si lo son, eso significa que es su turno
                    esMiTurno = true;
                    /* Reinicia el estado de la pregunta a false para que no indique si ya fue enviada
                    en caso de que venga de un turno anterior
                     */
                    preguntaEnviada = false;
                    // Habilitamos los campos
                    textFieldMensaje.setDisable(false);
                    buttonEnviar.setDisable(false);
                    agregarMensajeAlChat("Sistema", "Es tu turno, realiza tu pregunta");
                    // Si el nickname es el del oponente
                } else {
                    esMiTurno = false; // No es su turno
                    // Deshabilitamos los campos
                    textFieldMensaje.setDisable(true);
                    buttonEnviar.setDisable(true);
                    // Guardamos el nickname del oponente esto para que no haya confunciones con los turnos
                    nickNameOp = nickTurno;
                    agregarMensajeAlChat("Sistema", "Es el turno del jugador " + nickTurno);
                }
                // Vemos si el mensaje empieza por "PREGUNTA:"
            } else if (mensaje.startsWith("PREGUNTA:")) {
                // Viene estructurado de la siguiente manera: "PREGUNTA:nickname:mensaje"
                String[] mensajePartes = mensaje.split(":", 3); // Divide el mensaje en 3 partes
                if (mensajePartes.length >= 3) {
                    String jugadorPregunta = mensajePartes[1].trim(); // Este es el nickname de quien pregunto
                    String pregunta = mensajePartes[2].trim(); // El texto de la pregunta

                    // Esto lo agrego como adicional en caso de que intente duplicar la pregunta
                    if (!jugadorPregunta.equals(miNickname)) {
                        agregarMensajeAlChat(jugadorPregunta, pregunta); // Muestra la pregunta
                    }

                    // Si no es mi turno, significa que el oponente pregunto
                    if (!esMiTurno) {
                        textFieldMensaje.setDisable(false);
                        buttonEnviar.setDisable(false);
                        mostrarPregunta(pregunta);
                    }
                }
                // Vemos si el mensaje empieza por "RESPUESTA:"
            } else if (mensaje.startsWith("RESPUESTA:")) {
                // Viene estructurado de la siguiente manera: "RESPUESTA:nickname:mensaje"
                String[] mensajePartes = mensaje.split(":", 3); // Divide el mensjae en 3 partes
                if (mensajePartes.length >= 3) {
                    String jugadorRespuesta = mensajePartes[1].trim(); // Este es el nickname de quien responde
                    String respuesta = mensajePartes[2].trim(); // Este es el texto de la respuesta

                    // Evitamos duplicar las respuestas
                    if (!jugadorRespuesta.equals(miNickname)) {
                        agregarMensajeAlChat(jugadorRespuesta, respuesta); // Muestra la respuesta en el chat
                    }

                    // Si es mi turno (significa que yo hice la pregunta y el oponente me respondió)
                    if (esMiTurno) {
                        preguntaEnviada = false; // Ya recibí la respuesta, reinicio el estado de pregunta
                        agregarMensajeAlChat("Sistema", "Respuesta recibida. Ahora puedes voltear personajes o terminar tu turno.");
                        textFieldMensaje.setDisable(true); // Deshabilita el chat (ya pregunté y recibí respuesta)
                        buttonEnviar.setDisable(true);
                        // Aquí es donde un botón de "Terminar Turno"
                    }
                }
            } else if (mensaje.startsWith("RESULTADO")) {
                // // Viene estructurado de la siguiente manera: "RESULTADO:GANASTE:nickGanador"
                String [] partes = mensaje.split(":");
                String resultado = partes[1]; // GANASTE
                String nickGanador = partes[2];

                if (nickGanador.equals(miNickname)) {
                    System.out.println("Gane, GG WP");
                    TerminarPartidaController.estado = true; // Pantalla de ganaste
                } else {
                    System.out.println("Perdi, NT, MB");
                    TerminarPartidaController.estado = false; // Pantalla de perdiste
                }

                Platform.runLater(() -> {
                    try {
                        Parent root = FXMLLoader.load(getClass().getResource("/TerminarPartida/TerminarPartida.fxml"));
                        Scene scene = new Scene(root);
                        scene.getStylesheets().add(getClass().getResource("/TerminarPartida/TerminarPartidaStyles.css").toExternalForm());
                        Stage stage = new Stage();

                        // Obtenemos la ventana actual desde el rootPane creo
                        stage =  (Stage) rootPane.getScene().getWindow();

                        stage.hide();
                        stage.setScene(scene);
                        stage.show();
                    } catch (IOException ex){
                        ex.printStackTrace();
                    }


                });
            } else if (mensaje.startsWith("INICIAR_CRONOMETRO")) {
                agregarMensajeAlChat("Sistema", "Los jugadores han elegido personaje. QUE COMIENCE EL JUEGO ");
                reloj();
            } else if (mensaje.startsWith("OPONENTE DESCONECTADO:")) {
                // Viene estructurado de la siguiente manera: "OPONENTE DESCONECTADO:nickname"
                String nickNameDesconectado = mensaje.substring("OPONENTE DESCONECTADO:".length()).trim();
                agregarMensajeAlChat("Sistema", "Tu oponente (" + nickNameDesconectado + ") se ha desconectado :("); // Notifica al jugador

                // Deshabilitamos los campos
                textFieldMensaje.setDisable(true);
                buttonEnviar.setDisable(true);

                // Cerramos la conexion del cliente
                if (cliente != null) {
                    cliente.desconexion(); // Cierra la conexión del cliente
                }

                //AQUIIIIII DESPUES AGREGO LO DE VOLVER A LA SALA DE ESPERA

            } else {
                // Cualquier otro mensaje
                agregarMensajeAlChat("Sistema", "Mensaje del servidor: " + mensaje);
            }
        });
    }

    // Metodo para mostrar la pregunta que hizo el oponente
    private void mostrarPregunta(String pregunta){
        this.shadowPanePreguntas.setVisible(true); // Muestra el panel que simula la sombra de la pregunta
        this.gridPaneRespuestas.setVisible(true); // Muestra el gridPane que contiene la pregunta y los botones de respuesta

        this.labelPregunta.setText(pregunta); // Se le asigna la pregunta al label correspondiente
        this.labelPregunta.getStyleClass().add("labelPregunta"); // Se le asigna una clase de estilo al label
    }

    // Metodo para responder a la pregunta y termianr turno
    public void terminarTurno(ActionEvent e) {
        Button fuente = (Button) e.getSource(); // Se obtiene el botón que se haya presionado para responder

        if(fuente.getText().equals("SI")){ // Si el botón tiene como texto propio un "SI"
            this.textFieldMensaje.setText("SI"); // Se agrega al chat un "SI"
        } else{ // En caso de no tener un "SI" el botón
            this.textFieldMensaje.setText("NO"); // Se agrega al chat un "NO"
        }

        enviarMensaje(e); // Se envía el mensaje que se haya añadido al chat

        this.shadowPanePreguntas.setVisible(false); // Se oculta el panel que simula la sombra de las persguntas
        this.gridPaneRespuestas.setVisible(false); // Se oculta el gridPane de los botones de respuesta
    }

    // Metodo para contar el tiempo transcurrido de partida
    public void reloj() {
        // Se instancía un objeto de tipo TimeLine para llevar la cuenta
        Timeline timeline = new Timeline(new KeyFrame[]{new KeyFrame(Duration.seconds((double)1.0F), (event) -> {
            ++this.segundosTranscurridos; // Se aumentan los segundos transcurridos
            long horas = this.segundosTranscurridos / 3600L; // Se crea una variable para las horas
            long minutos = this.segundosTranscurridos % 3600L / 60L; // Se crea una variable para los minutos
            long segundos = this.segundosTranscurridos % 60L; // Se crea una variable para los segundos
            String tiempoFormateado = String.format("%02d:%02d:%02d", horas, minutos, segundos); // Se formatea el tiempo
            this.tiempoPartida.setText(tiempoFormateado); // Se le asigna el tiempo a el label correspondiente
        }, new KeyValue[0])});
        timeline.setCycleCount(-1);
        timeline.play(); // Se inicia el conteo
    }

    // Metodo para silenciar o reproducir música
    public void bottonMusica(ActionEvent e) {
        MenuController.musica.setMute(!MenuController.musica.isMute()); // Actualizamos la variable de estado de la clase Menu

        if (desicionUsuario) { // Si el usuario quiere silenciar la música
            desicionUsuario = false; // Invertimos la variable de estado local

            // Cargamos el ícono de música silenciada
            Image img = new Image(getClass().getResourceAsStream("/Menu/Assets/musicaMuteada.png"));
            ImageView imgV = new ImageView(img);
            imgV.setFitWidth(40);
            imgV.setFitHeight(40);
            buttonMusica.setGraphic(imgV);
        } else { // de lo contrario
            desicionUsuario = true; // Invertimos la variable de estado local

            // Cargamos el ícono de musica
            Image img = new Image(getClass().getResourceAsStream("/Menu/Assets/musica.png"));
            ImageView imgV = new ImageView(img);
            imgV.setFitWidth(40);
            imgV.setFitHeight(40);
            buttonMusica.setGraphic(imgV);
        }
    }

    // Metodo para reproducir el sonido de mensaje enviado
    public void sonidoEnviar(){
        sonidoMandar.setVolume(0.2);
        sonidoMandar.play();
    }

    // Metodo para cambiar la aplicación entre su modo pantalla completa y ventana
    @Override
    public void bottonCambiarModo(ActionEvent e) {
        super.bottonCambiarModo(e);
    }

    // Metodo para reproducir el sonido del teclado
    @Override
    public void sonidoTeclado() {
        super.sonidoTeclado();
    }

    // Metodo para cambiar de canción
    @Override
    public void ButtonSiguienteCancion(){
        super.ButtonSiguienteCancion();
    }
}