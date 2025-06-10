//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package Tablero;

import Classes.Personaje;
import Menu.Menu;
import Sockets.Cliente; // Importamos la clase Cliente
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.util.Duration;

import Sockets.Cliente.PersonajesListener;
import Sockets.Cliente.MensajeListener;

public class TableroController implements Initializable, PersonajesListener, MensajeListener {
    @FXML
    Pane rootPane;
    @FXML
    Label labelJugador;
    @FXML
    ImageView fondoImage;
    @FXML
    GridPane contentPane;
    @FXML
    TextFlow chat;
    @FXML
    TextField textFieldMensaje;
    @FXML
    Label tiempoPartida;
    @FXML
    GridPane gridTable;
    @FXML
    Button buttonEnviar;

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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Platform.runLater(() -> {
            Stage stage = (Stage)this.rootPane.getScene().getWindow();
            stage.setFullScreen(Menu.fullScreen);
        });

        this.fondoImage.fitWidthProperty().bind(this.rootPane.widthProperty());
        this.fondoImage.fitHeightProperty().bind(this.rootPane.heightProperty());
        this.contentPane.prefWidthProperty().bind(this.rootPane.widthProperty());
        this.contentPane.prefHeightProperty().bind(this.rootPane.heightProperty());
        this.labelJugador.setText(Menu.nickName);
        this.reloj();

        // Estos son los que se van a eliminar para obtener de la base de datos
        this.cargarImagenes();
        this.insertarPersonaje();

        // El campo de mensaje y el boton para enviar el mensaje se encuentran desabilitados
        // Hasta que se le asigne un turno o se reciba una pregunta
        textFieldMensaje.setDisable(true);
        buttonEnviar.setDisable(true);
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

            // AQUI SE AGREGA LO DEL PERSONAJE SECRETO SEGUN YO POR QUE ES DESPUES DE LA GENERACION DEL TABLERO

        });
    }

    //Esta la configuaran para que muestre los personajes en el tablero
    public void mostrarPersonajesEnTablero(List<Personaje> personajes) {

    }


    private void cargarImagenes() {
        this.imagenes.clear();

        for(int i = 0; i < 24; ++i) {
            String ruta = "/Tablero/Assets/Personaje" + i + ".jpg";
            Image img = new Image(this.getClass().getResourceAsStream(ruta));
            this.imagenes.add(img);
        }

    }

    private void insertarPersonaje() {
        int indice = 0;

        for(int i = 0; i < 4; i++) {
            for(int j = 0; j < 6; j++) {
                Image img = (Image)this.imagenes.get(indice);
                ImageView imageView = new ImageView(img);
                imageView.fitWidthProperty().bind(this.gridTable.widthProperty().divide(6).subtract(4));
                imageView.fitHeightProperty().bind(this.gridTable.heightProperty().divide(4).subtract(4));
                imageView.setPreserveRatio(false);
                this.gridTable.add(imageView, j, i);
                indice++;
            }
        }
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