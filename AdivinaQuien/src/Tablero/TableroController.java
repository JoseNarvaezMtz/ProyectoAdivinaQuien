//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package Tablero;

//HEY HOLA CHULAS JIJIJIJIJI
//TIENEN Q AGREGAR LOS MÓDULOS DE AUDIO AL PROYECTO UWU
// Son estos :3
//  --add-modules javafx.controls,javafx.fxml,javafx.media --add-exports javafx.base/com.sun.javafx=ALL-UNNAMED

import Classes.Personaje;
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

public class TableroController extends MenuController implements Initializable, PersonajesListener, MensajeListener {

    @FXML GridPane contentPane;
    @FXML GridPane gridTable;
    @FXML GridPane sideBarPane;
    @FXML GridPane seleccionPersonaje;
    @FXML GridPane gridPaneListaPer;
    @FXML GridPane contenedorListaPreguntas;
    @FXML GridPane gridPanePreguntas;
    @FXML GridPane contenedorListaPer;
    @FXML GridPane btnsContainer;
    @FXML GridPane gridPaneRespuestas;
    @FXML Pane shadowPanePreguntas;
    @FXML Pane rootPane;
    @FXML Pane shadowPanePersonajes;

    @FXML ImageView fondoImage;
    @FXML ImageView dadosImg;
    @FXML ImageView userImg;
    @FXML ImageView userRival;
    @FXML ImageView listaImg;

    @FXML Label labelPregunta;
    @FXML Label labelJugador;
    @FXML Label tiempoPartida;
    @FXML Label labelFecha;

    @FXML TextFlow chat;
    @FXML TextField textFieldMensaje;

    @FXML Button buttonEnviar;
    @FXML Button btnLstaPreguntas;
    @FXML Button btnRespNo;
    @FXML Button btnRespSi;
    @FXML Button buttonMusica;
    @FXML Button buttonModo;
    @FXML Button buttonSalir;
    @FXML Button buttonSiguienteMusica;


    private final List<Image> imagenes = new ArrayList();
    private long segundosTranscurridos = 0L;

    // ---------------------- ATRIBUTOS PARA MANEJAR EL TURNO Y EL CHAT -------------------------
    private Cliente cliente; // Cliente para comunicarse con el servidor
    private boolean esMiTurno = false; // Booleano para indicar turnos
    private boolean preguntaEnviada = false; // Booleano para saber si ya se envio la pregunta
    private String miNickname = Menu.nickName; // Cadena para saber mi nickname
    private String nickNameOp = ""; // Cadena para saber el nickname del oponente

    private List<Personaje> personajesJuego; // Lista para almacenar la lista del servidor

    public int idPersonaje; // Id del personaje del usuario
    private int volteados = 0;

    //Sonidos
    public static AudioClip sonidoVoltear;
    public static AudioClip sonidoMandar;
    public static AudioClip sonidoAdivinar;
    public static AudioClip sonidoBloqueado;
    public static AudioClip sonidoDados;
    public static AudioClip sonidoTablero;
    public static AudioClip sonidoLista;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        //Sonidos
        sonidoVoltear = new AudioClip(getClass().getResource("/Tablero/Assets/door.mp3").toString());
        sonidoMandar = new AudioClip(getClass().getResource("/Tablero/Assets/send.mp3").toString());
        sonidoAdivinar = new AudioClip(getClass().getResource("/Tablero/Assets/confirm.wav").toString());
        sonidoBloqueado = new AudioClip(getClass().getResource("/Tablero/Assets/blocked.wav").toString());
        sonidoDados = new AudioClip(getClass().getResource("/Tablero/Assets/dice.mp3").toString());
        sonidoTablero = new AudioClip(getClass().getResource("/Tablero/Assets/confirmTab.mp3").toString());
        sonidoLista = new AudioClip(getClass().getResource("/Tablero/Assets/paper.mp3").toString());

        //Musica
        inicializarMusica();

        Platform.runLater(() -> {
            Stage stage = (Stage)this.rootPane.getScene().getWindow();
            stage.setFullScreen(Menu.fullScreen);
        });

        LocalDate fechaActual = LocalDate.now();
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        String fechaFormato = fechaActual.format(formato);
        labelFecha.setText(fechaFormato);
        labelFecha.getStyleClass().add("labelTiempo");
        labelJugador.getStyleClass().add("labelTiempo");

        this.sideBarPane.setVisible(false);

        this.fondoImage.fitWidthProperty().bind(this.rootPane.widthProperty());
        this.fondoImage.fitHeightProperty().bind(this.rootPane.heightProperty());
        this.contentPane.prefWidthProperty().bind(this.rootPane.widthProperty());
        this.contentPane.prefHeightProperty().bind(this.rootPane.heightProperty());
        this.labelJugador.setText(Menu.nickName);
        this.contenedorListaPer.prefWidthProperty().bind(this.contentPane.widthProperty());
        this.contenedorListaPer.prefHeightProperty().bind(this.contentPane.heightProperty());
        this.shadowPanePersonajes.prefWidthProperty().bind(this.contentPane.widthProperty());
        this.shadowPanePersonajes.prefHeightProperty().bind(this.contentPane.heightProperty());
        this.contenedorListaPreguntas.prefWidthProperty().bind(this.contentPane.widthProperty());
        this.contenedorListaPreguntas.prefHeightProperty().bind(this.contentPane.heightProperty());
        this.shadowPanePreguntas.prefWidthProperty().bind(this.contentPane.widthProperty());
        this.shadowPanePreguntas.prefHeightProperty().bind(this.contentPane.heightProperty());
        this.gridPaneRespuestas.prefWidthProperty().bind(this.contentPane.widthProperty());
        this.gridPaneRespuestas.prefHeightProperty().bind(this.contentPane.heightProperty());
        this.btnsContainer.prefWidthProperty().bind(this.contentPane.widthProperty().multiply(.8));
        this.btnsContainer.prefHeightProperty().bind(this.contentPane.heightProperty().multiply(.5));


        this.textFieldMensaje.prefWidthProperty().bind(this.rootPane.widthProperty().multiply(.245));

        this.buttonEnviar.prefWidthProperty().bind(rootPane.widthProperty().multiply(0.0525));
        this.buttonEnviar.prefHeightProperty().bind(rootPane.heightProperty().multiply(0.06));
        this.btnLstaPreguntas.prefWidthProperty().bind(rootPane.widthProperty().multiply(0.04));
        this.btnLstaPreguntas.prefHeightProperty().bind(rootPane.heightProperty().multiply(0.06));

        this.btnRespNo.prefWidthProperty().bind(rootPane.widthProperty().divide(10));
        this.btnRespNo.prefHeightProperty().bind(rootPane.heightProperty().divide(8));
        this.btnRespSi.prefWidthProperty().bind(rootPane.widthProperty().divide(10));
        this.btnRespSi.prefHeightProperty().bind(rootPane.heightProperty().divide(8));

        // El campo de mensaje y el boton para enviar el mensaje se encuentran desabilitados
        // Hasta que se le asigne un turno o se reciba una pregunta
        textFieldMensaje.setDisable(true);
        buttonEnviar.setDisable(true);

        this.userImg.fitWidthProperty().bind(this.rootPane.widthProperty().divide(6.5));
        this.userImg.fitHeightProperty().bind(this.rootPane.heightProperty().divide(4.5));

        this.userRival.fitWidthProperty().bind(this.rootPane.widthProperty().divide(6.5));
        this.userRival.fitHeightProperty().bind(this.rootPane.heightProperty().divide(4.5));

        this.dadosImg.setOnMouseEntered(mouseEvent -> {imgMouseEntered(mouseEvent);});
        this.dadosImg.setOnMouseExited(mouseEvent -> {imgMouseExited(mouseEvent);});

        this.listaImg.fitWidthProperty().bind(this.rootPane.widthProperty().divide(6.5));
        this.listaImg.fitHeightProperty().bind(this.rootPane.heightProperty().divide(4.5));

        this.listaImg.setOnMouseEntered(mouseEvent -> {imgMouseEntered(mouseEvent);});
        this.listaImg.setOnMouseExited(mouseEvent -> {imgMouseExited(mouseEvent);});

        Image imagenEnviar = new Image(getClass().getResourceAsStream("/Tablero/Assets/mouseClick.png"));
        ImageView imageView = new ImageView(imagenEnviar);
        imageView.setFitWidth(35);
        imageView.setFitHeight(35);
        buttonEnviar.setGraphic(imageView);

        Image imagenPregunta = new Image(getClass().getResourceAsStream("/Tablero/Assets/lista.png"));
        ImageView imageView1 = new ImageView(imagenPregunta);
        imageView1.setFitWidth(35);
        imageView1.setFitHeight(35);
        btnLstaPreguntas.setGraphic(imageView1);

        Image imagenModo= new Image(getClass().getResourceAsStream("/Menu/Assets/maximizar.png"));
        ImageView imageView2 = new ImageView(imagenModo);
        imageView2.setFitWidth(40);
        imageView2.setFitHeight(40);
        buttonModo.setGraphic(imageView2);

        Image imagenSalir = new Image(getClass().getResourceAsStream("/Tablero/Assets/salir.png"));
        ImageView imageView3 = new ImageView(imagenSalir);
        imageView3.setFitWidth(35);
        imageView3.setFitHeight(35);
        buttonSalir.setGraphic(imageView3);

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

        elegirPersonaje();
    }

    public void listaPreguntas(){
        this.shadowPanePreguntas.setVisible(true);
        this.contenedorListaPreguntas.setVisible(true);
        cargarListaPreguntas();
    }

    public void salirPreguntas(){
        this.shadowPanePreguntas.setVisible(false);
        this.contenedorListaPreguntas.setVisible(false);
    }

    private void cargarListaPreguntas() {
        List<String> preguntasDesdeDB = PreguntasDB.obtenerPreguntas();
        ObservableList<String> preguntasObservables = FXCollections.observableArrayList(preguntasDesdeDB);

        ListView<String> listViewPreguntas = new ListView<>(preguntasObservables);
        listViewPreguntas.getStyleClass().add("listView");
        listViewPreguntas.setOrientation(Orientation.VERTICAL);
        listViewPreguntas.setStyle("-fx-font-family: Cherry Bomb One;");
        listViewPreguntas.setStyle("-fx-font-size: 14px;");

        Consumer<String> accionSeleccionarPregunta = pregunta -> {
            textFieldMensaje.setText(pregunta);
            salirPreguntas();
        };


        listViewPreguntas.setCellFactory(param -> new ListCell<String>() {
            private final BorderPane layout = new BorderPane();
            private final Label labelPregunta = new Label();
            private final Button botonPreguntar = new Button("Preguntar");

            {
                layout.setLeft(labelPregunta);
                layout.setRight(botonPreguntar);
                BorderPane.setMargin(labelPregunta, new Insets(0, 10, 0, 0));

                botonPreguntar.setOnAction(event -> {
                    if (getItem() != null && !getItem().isEmpty()) {
                        accionSeleccionarPregunta.accept(getItem());
                    }
                });
            }

            @Override
            protected void updateItem(String pregunta, boolean empty) {
                super.updateItem(pregunta, empty);

                if (empty || pregunta == null) {
                    setGraphic(null);
                } else {
                    labelPregunta.setText(pregunta);
                    labelPregunta.setWrapText(true);
                    setGraphic(layout);
                }
            }
        });

        gridPanePreguntas.add(listViewPreguntas, 0, 1);
    }

    private void cargarListaPersonajes() {
        ObservableList<Personaje> personajes = FXCollections.observableArrayList(personajesJuego);
        ListView<Personaje> listViewPersonajes = new ListView((ObservableList)personajes);
        listViewPersonajes.setOrientation(Orientation.VERTICAL);

        listViewPersonajes.setCellFactory(param -> new ListCell<Personaje>(){
            private final ImageView imageView = new ImageView();
            private final Label label = new Label();
            private final VBox layout = new VBox(10, imageView, label);

            @Override
            protected void updateItem(Personaje personaje, boolean empty){
                super.updateItem(personaje, empty);

                if(empty || personaje == null){
                    setText(null);
                    setGraphic(null);
                } else {
                    Image img = personaje.getImagenFX();
                    imageView.setImage(img);
                    imageView.setFitWidth(100);
                    imageView.setPreserveRatio(true);
                    label.setText(personaje.getNombre());
                    layout.setAlignment(Pos.CENTER);
                    setGraphic(layout);
                }
            }
        });

        listViewPersonajes.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                Image img = newSelection.getImagenFX();
                this.userImg.setImage(img);
                this.idPersonaje = newSelection.getIdTablero() + 1;
                System.out.println(this.idPersonaje);
                cerrarListaPersonajes(null);
                this.seleccionPersonaje.setVisible(false);
                this.sideBarPane.setVisible(true);

                contenedorListaPer.setVisible(false);
                shadowPanePersonajes.setVisible(false);
                reasignarMetodos();
            }
        });

        gridPaneListaPer.add(listViewPersonajes, 0, 1);
    }

    private void elegirPersonaje(){
        this.seleccionPersonaje.setVisible(true);

        this.dadosImg.fitWidthProperty().bind(this.rootPane.widthProperty().divide(6));
        this.dadosImg.fitHeightProperty().bind(this.rootPane.heightProperty().divide(4));
    }

    public void eleccionTablero(MouseEvent e){
        ImageView imgView = (ImageView)e.getSource();
        sonidoTablero.setVolume(0.2);
        sonidoTablero.play();

        imgView.setEffect(null);

        Image img = imgView.getImage();
        this.userImg.setImage(img);

        this.seleccionPersonaje.setVisible(false);
        this.sideBarPane.setVisible(true);

        reasignarMetodos();

        this.idPersonaje = gridTable.getChildren().indexOf(imgView);

        this.reloj();

    }

    public void eleccionRandom(){
        Random random = new Random();

        sonidoDados.setVolume(0.2);
        sonidoDados.play();

        this.dadosImg.setEffect(null);
        this.dadosImg.setOnMouseEntered(null);
        this.dadosImg.setOnMouseExited(null);

        int rand = random.nextInt(24)+1;

        StackPane stackPane = (StackPane)this.gridTable.getChildren().get(rand);
        ImageView imgView = (ImageView)stackPane.getChildren().get(0);

        Image img = imgView.getImage();
        this.userImg.setImage(img);

        this.seleccionPersonaje.setVisible(false);
        this.sideBarPane.setVisible(true);

        reasignarMetodos();
        this.reloj();
    }

    public void eleccionLista(){
        sonidoLista.setVolume(0.2);
        sonidoLista.play();

        contenedorListaPer.setVisible(true);
        shadowPanePersonajes.setVisible(true);
    }

    public void cerrarListaPersonajes(ActionEvent e){
        contenedorListaPer.setVisible(false);
        shadowPanePersonajes.setVisible(false);
    }

    private void reasignarMetodos(){
        for(int i=1; i<25; i++){
            this.gridTable.getChildren().get(i).setOnMouseClicked(null);
            this.gridTable.getChildren().get(i).setOnMouseEntered(mouseEvent -> {mouseEntro(mouseEvent);});
            this.gridTable.getChildren().get(i).setOnMouseExited(mouseEvent -> {mouseSalio(mouseEvent);});

            StackPane stack = (StackPane)this.gridTable.getChildren().get(i);

            for (Node hijo : stack.getChildren()) {
                if (hijo instanceof ImageView) {
                    hijo.setOnMouseClicked(null);
                    hijo.setOnMouseEntered(null);
                    hijo.setOnMouseExited(null);
                    break;
                }
            }
        }
    }

    private void mouseEntro(MouseEvent e){
        StackPane stack = (StackPane)e.getSource();
        if(stack.getChildren().getFirst() instanceof ImageView) {
            Image img = ((ImageView) stack.getChildren().getFirst()).getImage();
            int indice = imagenes.indexOf(img);

            int row = 0, col = 0;

            if (indice > 5) {
                row = indice / 6;
                col = indice % 6;
            } else {
                row = 0;
                col = indice;
            }

            BorderPane menuPersonajeContenedor = new BorderPane();
            GridPane menuPersonaje = new GridPane();

            menuPersonaje.setAlignment(Pos.CENTER);
            menuPersonaje.setHgap(4);

            Image iconAdivinar = new Image(getClass().getResourceAsStream("/Tablero/Assets/adivinar.png"));
            Image iconVoltear = new Image(getClass().getResourceAsStream("/Tablero/Assets/voltear.png"));

            ImageView iconAdivinarIV = new ImageView(iconAdivinar);
            ImageView iconVoltearIV = new ImageView(iconVoltear);

            iconAdivinarIV.setFitWidth(24);
            iconAdivinarIV.setFitHeight(24);

            iconVoltearIV.setFitWidth(24);
            iconVoltearIV.setFitHeight(24);

            Button botonVoltear = new Button();
            botonVoltear.setMaxWidth(Double.MAX_VALUE);
            botonVoltear.setGraphic(iconVoltearIV);

            Button botonAdivinar = new Button();

            botonVoltear.setMaxWidth(Double.MAX_VALUE);
            botonAdivinar.setGraphic(iconAdivinarIV);

            botonVoltear.prefWidthProperty().bind(rootPane.widthProperty().divide(35));
            botonVoltear.prefHeightProperty().bind(rootPane.heightProperty().divide(25));

            botonAdivinar.prefWidthProperty().bind(rootPane.widthProperty().divide(35));
            botonAdivinar.prefHeightProperty().bind(rootPane.heightProperty().divide(25));

            Effect sombra = new DropShadow();

            botonAdivinar.setEffect(sombra);
            botonVoltear.setEffect(sombra);

            botonAdivinar.setOnMouseClicked(mouseEvent -> {
                try {
                    adivinar(mouseEvent, indice);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            });
            botonVoltear.setOnMouseClicked(mouseEvent -> {
                try {
                    voltear(mouseEvent, indice);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            });

            if(personajesJuego.get(indice).isTachado()){
                botonAdivinar.setDisable(true);
            } else{
                botonAdivinar.setDisable(false);
            }

            menuPersonaje.add(botonVoltear, 0, 0);
            menuPersonaje.add(botonAdivinar, 1, 0);
            menuPersonajeContenedor.setBottom(menuPersonaje);

            stack.getChildren().add(menuPersonajeContenedor);
        }
    }

    private void mouseSalio(MouseEvent e){
        StackPane stack = (StackPane)e.getSource();

        for (Node hijo : stack.getChildren()) {
            if (hijo instanceof BorderPane) {
                stack.getChildren().remove(hijo);
                break;
            }
        }
    }

    private void voltear(MouseEvent e, int indice) throws IOException {
        StackPane stack = (StackPane)gridTable.getChildren().get(indice+1);

        for (Node hijo : stack.getChildren()) {
            if (hijo instanceof ImageView) {
                if(personajesJuego.get(indice).isTachado()){
                    hijo.setEffect(null);
                } else{
                    Effect efecto = new SepiaTone();
                    hijo.setEffect(efecto);
                }
                sonidoBloqueado.setVolume(0.2);
                sonidoBloqueado.play();
                break;
            }
            if (hijo instanceof BorderPane){
                GridPane grid = (GridPane)((BorderPane) hijo).getBottom();

                if(personajesJuego.get(indice).isTachado()) grid.getChildren().get(1).setDisable(true);
                else grid.getChildren().get(1).setDisable(true);

                System.out.println(grid.getChildren().get(0));
                System.out.println(grid.getChildren().get(1));

                sonidoVoltear.setVolume(0.2);
                sonidoVoltear.play();
                break;
            }
        }

        if(personajesJuego.get(indice).isTachado()){
            personajesJuego.get(indice).setTachado(false);
            this.volteados--;
        } else{
            personajesJuego.get(indice).setTachado(true);
            this.volteados++;
            if(this.volteados == 24){
                TerminarPartidaController.estado = false;
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
    }

    private void adivinar(MouseEvent e, int indice) throws IOException {
        sonidoAdivinar.setVolume(0.2);
        sonidoAdivinar.play();

        // QUE EL CONTADOR NO EMPIECE HASTA QUE LOS DOS HAYAN ELEGIDO PERSONAJE

        // QUE JALEN LOS TURNOS

        // QUE SE CARGUE EL NICKNAME DEL OTRO JUGADOR

        // 1.- MANDAR POR SOCKETS HACIA EL SERVER, Y DE ALGUNA MANERA DEBES CHECAR SI ESE ÍNDICE ES IGUAL AL IDPERSONAJE DEL OTRO JUGADOR

        // 2.- SI ES IGUAL, ENTONCES HACES TRU EL BOOLEANO DE LA PARTIDA TERMINADA, SI NO, LO HACES FALSE

        Parent root = FXMLLoader.load(getClass().getResource("/TerminarPartida/TerminarPartida.fxml"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/TerminarPartida/TerminarPartidaStyles.css").toExternalForm());
        Stage stage = new Stage();
        stage = (Stage)((Node) e.getSource()).getScene().getWindow();
        stage.hide();
        stage.setScene(scene);
        stage.show();
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;

        // Configuramos listeners
        this.cliente.setMensajeListener(this::onManejarMensajeServidor);
        this.cliente.setPersonajeListener(this::onPersonajesRecibidos);
        System.out.println("TableroController: Cliente asignado y listeners configurados");
    }

    // ------------------ IMPLEMENTACION DE LOS LISTENER ------------------
    public void onPersonajesRecibidos(List<Personaje> personajes) {
        Platform.runLater(() -> {
            System.out.println("TableroController: Lista de personajes recibida");
            this.personajesJuego = personajes; // Guardamos la lista

            // Mostramos los personajes en el tablero
            cargarListaImagenes();

            mostrarPersonajesEnTablero();

            cargarListaPersonajes();
        });
    }

    private void cargarListaImagenes(){
        for(int i=0; i<(gridTable.getRowCount()*gridTable.getColumnCount()); i++){
            this.imagenes.add(personajesJuego.get(i).getImagenFX());
        }
    }

    //Esta la configuaran para que muestre los personajes en el tablero
    public void mostrarPersonajesEnTablero(){
        int indice = 0;

        for(int i = 0; i < 4; i++) {
            for(int j = 0; j < 6; j++) {
                Image img = (Image)this.imagenes.get(indice);
                ImageView imageView = new ImageView(img);
                imageView.fitWidthProperty().bind(this.gridTable.widthProperty().divide(6).subtract(4));
                imageView.fitHeightProperty().bind(this.gridTable.heightProperty().divide(4).subtract(4));
                imageView.setPreserveRatio(false);
                imageView.setOnMouseClicked(mouseEvent -> {eleccionTablero(mouseEvent);});
                imageView.setOnMouseEntered(mouseEvent -> {imgMouseEntered(mouseEvent);});
                imageView.setOnMouseExited(mouseEvent -> {imgMouseExited(mouseEvent);});

                StackPane stack = new StackPane();
                stack.getChildren().add(imageView);
                this.gridTable.add(stack, j, i);
                indice++;
            }
        }
    }

    private void imgMouseEntered(MouseEvent e){
        ImageView imgView = (ImageView)e.getSource();
        Effect effect = new Glow(0.6);
        imgView.setEffect(effect);
    }

    private void imgMouseExited(MouseEvent e){
        ImageView imgView = (ImageView)e.getSource();
        imgView.setEffect(null);
    }

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
            Text nickText = new Text(nick + ": ");
            nickText.setFont(Font.font("System", FontWeight.BOLD, 16));

            Text mensajeText = new Text(mensaje + "\n");
            mensajeText.setFont(Font.font("System", FontWeight.NORMAL, 14));

            chat.getChildren().addAll(nickText, mensajeText);

            //AQUI PODEMOS AGREGAR EL SCROLL
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

    private void mostrarPregunta(String pregunta){
        this.shadowPanePreguntas.setVisible(true);
        this.gridPaneRespuestas.setVisible(true);

        this.labelPregunta.setText(pregunta);
        this.labelPregunta.getStyleClass().add("labelPregunta");
    }

    public void terminarTurno(ActionEvent e) {
        Button fuente = (Button) e.getSource();

        if(fuente.getText().trim() == "SI"){
            this.textFieldMensaje.setText("SI");
        } else{
            this.textFieldMensaje.setText("NO");
        }

        enviarMensaje(e);

        this.shadowPanePreguntas.setVisible(false);
        this.gridPaneRespuestas.setVisible(false);
    }

    public void reloj() {
        Timeline timeline = new Timeline(new KeyFrame[]{new KeyFrame(Duration.seconds((double)1.0F), (event) -> {
            ++this.segundosTranscurridos;
            long horas = this.segundosTranscurridos / 3600L;
            long minutos = this.segundosTranscurridos % 3600L / 60L;
            long segundos = this.segundosTranscurridos % 60L;
            String tiempoFormateado = String.format("%02d:%02d:%02d", horas, minutos, segundos);
            this.tiempoPartida.setText(tiempoFormateado);
        }, new KeyValue[0])});
        timeline.setCycleCount(-1);
        timeline.play();
    }

    public void bottonMusica(ActionEvent e) {
        MenuController.musica.setMute(!MenuController.musica.isMute());
        // Cambiamos la decisión del usuario
        if (desicionUsuario) {
            desicionUsuario = false;
            Image img = new Image(getClass().getResourceAsStream("/Menu/Assets/musicaMuteada.png"));
            ImageView imgV = new ImageView(img);
            imgV.setFitWidth(40);
            imgV.setFitHeight(40);
            buttonMusica.setGraphic(imgV);
        } else {
            desicionUsuario = true;
            Image img = new Image(getClass().getResourceAsStream("/Menu/Assets/musica.png"));
            ImageView imgV = new ImageView(img);
            imgV.setFitWidth(40);
            imgV.setFitHeight(40);
            buttonMusica.setGraphic(imgV);
        }
    }

    public void sonidoEnviar(){
        sonidoMandar.setVolume(0.2);
        sonidoMandar.play();
    }

    @Override
    public void bottonCambiarModo(ActionEvent e) {
        super.bottonCambiarModo(e);
    }

    @Override
    public void sonidoTeclado() {
        super.sonidoTeclado();
    }

    @Override
    public void ButtonSiguienteCancion(){
        super.ButtonSiguienteCancion();
    }
}