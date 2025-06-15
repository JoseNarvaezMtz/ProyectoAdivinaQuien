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
import DataBaseClasses.PersonajeDB;
import Menu.Menu;
import Sockets.Cliente; // Importamos la clase Cliente

import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.URL;
import java.util.*;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.util.Duration;
import Menu.MenuController;


import Sockets.Cliente.PersonajesListener;
import Sockets.Cliente.MensajeListener;

public class TableroController implements Initializable, PersonajesListener, MensajeListener {

    @FXML Pane rootPane;
    @FXML GridPane contentPane;
    @FXML GridPane gridTable;
    @FXML GridPane sideBarPane;
    @FXML GridPane seleccionPersonaje;
    @FXML GridPane gridPaneListaPer;
    @FXML GridPane contenedorListaPer;
    @FXML Pane shadowPane;

    @FXML ImageView fondoImage;
    @FXML ImageView dadosImg;
    @FXML ImageView userImg;
    @FXML ImageView listaImg;

    @FXML Label labelJugador;
    @FXML Label tiempoPartida;

    @FXML TextFlow chat;
    @FXML TextField textFieldMensaje;

    @FXML Button buttonEnviar;



    private List<Image> imagenes = new ArrayList();
    private long segundosTranscurridos = 0L;

    // ---------------------- ATRIBUTOS PARA MANEJAR EL TURNO Y EL CHAT -------------------------
    private Cliente cliente; // Cliente para comunicarse con el servidor
    private boolean esMiTurno = false; // Booleano para indicar turnos
    private boolean preguntaEnviada = false; // Booleano para saber si ya se envio la pregunta
    private String miNickname = Menu.nickName; // Cadena para saber mi nickname
    private String nickNameOp = ""; // Cadena para saber el nickname del oponente

    private List<Personaje> personajesJuego; // Lista para almacenar la lista del servidor
    private Personaje miPersonaje; // Personaje que se debe de adivinar

    public int idPersonaje; // Id del personaje del usuario

    //Música
    public static MediaPlayer musica;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        //Musica
        Media music = new Media(getClass().getResource("/Tablero/Assets/music1.mp3").toString());
        musica = new MediaPlayer(music);

        if(MenuController.desicionUsuario == true){
            musica.setCycleCount(MediaPlayer.INDEFINITE);
            musica.play();
        }

        Platform.runLater(() -> {
            Stage stage = (Stage)this.rootPane.getScene().getWindow();
            stage.setFullScreen(Menu.fullScreen);
        });

        this.sideBarPane.setVisible(false);

        this.fondoImage.fitWidthProperty().bind(this.rootPane.widthProperty());
        this.fondoImage.fitHeightProperty().bind(this.rootPane.heightProperty());
        this.contentPane.prefWidthProperty().bind(this.rootPane.widthProperty());
        this.contentPane.prefHeightProperty().bind(this.rootPane.heightProperty());
        this.labelJugador.setText(Menu.nickName);
        this.contenedorListaPer.prefWidthProperty().bind(this.contentPane.widthProperty());
        this.contenedorListaPer.prefHeightProperty().bind(this.contentPane.heightProperty());
        this.shadowPane.prefWidthProperty().bind(this.contentPane.widthProperty());
        this.shadowPane.prefHeightProperty().bind(this.contentPane.heightProperty());

        // El campo de mensaje y el boton para enviar el mensaje se encuentran desabilitados
        // Hasta que se le asigne un turno o se reciba una pregunta
        textFieldMensaje.setDisable(true);
        buttonEnviar.setDisable(true);

        this.userImg.fitWidthProperty().bind(this.rootPane.widthProperty().divide(6.5));
        this.userImg.fitHeightProperty().bind(this.rootPane.heightProperty().divide(4.5));

        this.dadosImg.setOnMouseEntered(mouseEvent -> {imgMouseEntered(mouseEvent);});
        this.dadosImg.setOnMouseExited(mouseEvent -> {imgMouseExited(mouseEvent);});

        this.listaImg.fitWidthProperty().bind(this.rootPane.widthProperty().divide(6.5));
        this.listaImg.fitHeightProperty().bind(this.rootPane.heightProperty().divide(4.5));

        this.listaImg.setOnMouseEntered(mouseEvent -> {imgMouseEntered(mouseEvent);});
        this.listaImg.setOnMouseExited(mouseEvent -> {imgMouseExited(mouseEvent);});

        elegirPersonaje();
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

        this.dadosImg.setEffect(null);
        this.dadosImg.setOnMouseEntered(null);
        this.dadosImg.setOnMouseExited(null);

        int rand = random.nextInt(24)+1;

        ImageView imgView = (ImageView)this.gridTable.getChildren().get(rand);

        Image img = imgView.getImage();
        this.userImg.setImage(img);

        this.seleccionPersonaje.setVisible(false);
        this.sideBarPane.setVisible(true);

        reasignarMetodos();
        this.reloj();
    }

    public void eleccionLista(){
        contenedorListaPer.setVisible(true);
        shadowPane.setVisible(true);
    }

    public void cerrarListaPersonajes(ActionEvent e){
        contenedorListaPer.setVisible(false);
        shadowPane.setVisible(false);
    }

    private void reasignarMetodos(){
        for(int i=1; i<=24; i++){
            this.gridTable.getChildren().get(i).setOnMouseClicked(null);
            this.gridTable.getChildren().get(i).setOnMouseEntered(mouseEvent -> {mouseEntro(mouseEvent);});
            this.gridTable.getChildren().get(i).setOnMouseExited(mouseEvent -> {mouseSalio(mouseEvent);});
        }
    }

    private void mouseEntro(MouseEvent e){
        ImageView imgV = (ImageView)e.getSource();
        Image img = imgV.getImage();
        int indice = imagenes.indexOf(img);

        int row = 0, col = 0;

        if(indice > 5){
            row = indice / 6;
            col = indice % 6;
        } else{
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

        menuPersonaje.add(botonVoltear, 0, 0);
        menuPersonaje.add(botonAdivinar, 1,   0);
        menuPersonajeContenedor.setBottom(menuPersonaje);

        this.gridTable.add(menuPersonajeContenedor, col, row);
    }

    private void mouseSalio(MouseEvent e){
        ImageView imgV = (ImageView)e.getSource();
        Image img = imgV.getImage();
        int indice = imagenes.indexOf(img);


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
            mostrarPersonajesEnTablero(personajes);

            cargarListaImagenes();

            cargarListaPersonajes();
        });
    }

    private void cargarListaImagenes(){
        for(int i=0; i<(gridTable.getRowCount()*gridTable.getColumnCount()); i++){
            this.imagenes.add(personajesJuego.get(i).getImagenFX());
        }
    }

    //Esta la configuaran para que muestre los personajes en el tablero
    public void mostrarPersonajesEnTablero(List<Personaje> personajes){
        for (int i = 0; i < 24; i++) {
            byte[] ruta = personajes.get(i).getImagen();
            Image img = new Image(new ByteArrayInputStream(ruta));
            this.imagenes.add(img);
        }

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
                this.gridTable.add(imageView, j, i);
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

    private void insertarPersonaje() {

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
                        agregarMensajeAlChat("Sistema", "El jugador oponente ha hceho una pregunta, responde (Si o No) paro");
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
}