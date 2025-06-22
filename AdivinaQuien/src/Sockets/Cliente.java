package Sockets;

import Classes.Partida;
import DataBaseClasses.PersonajeDB;
import javafx.application.Platform;
import Menu.Menu;
import Classes.Personaje;
import java.io.*;
import java.net.*;
import java.util.*;

public class Cliente {
    // ----------------- ATRIBUTOS -------------------------
    // Atributos para la conexión y comunicación por objetos.
    private Socket socket;
    private ObjectOutputStream objectOut;
    private ObjectInputStream objectIn;

    // Listeners para comunicar con la Interfaz Gráfica (UI).
    private MensajeListener mensajeListener;
    private ClienteListener clienteListener;
    private PersonajesListener personajesListener;

    // Almacenes temporales para datos que llegan antes de que la UI esté lista.
    private String mensajePendiente = null;
    private List <Personaje> personajesPendientes = null;

    // Mensajes pendientes del oponente
    private String oponentePendiente = null;

    //Creamos la partida para la base de datos
    public static Partida partidaCliente;

    // ----------------- CONSTRUCTOR -----------------
    // Aqui se manejan los mensajes hacia el servidor y las cosas que se mandan al tablero
    public Cliente(String host, int puerto, ClienteListener clienteListener) throws IOException {
        this.clienteListener = clienteListener;
        socket = new Socket(host, puerto);

        // Para evitar un bloqueo (deadlock), el cliente debe crear
        // el InputStream PRIMERO y el OutputStream DESPUÉS.
        this.objectOut = new ObjectOutputStream(socket.getOutputStream());
        // Forzamos el envío del encabezado del stream para completar el "saludo" al servidor.
        this.objectOut.flush();

        this.objectIn = new ObjectInputStream(socket.getInputStream());

        // ----------------- Enviamos el nickname al servidor despues de conenctar -----------------
        // Esto por que es lo primero que espera el servidor
        String nickAEnviar;
        if (Menu.nickName != null && !Menu.nickName.isEmpty()) {
            nickAEnviar = Menu.nickName.trim();
        } else {
            System.err.println("Warning xd: El nickname no esta establecido en la clase Menu.");
            nickAEnviar = "Unknown Player";
        }
        /* Se usa un metodo aparte para optimizar el codigo dicho de otra manera lo hacemos
        por que como es lo primero que espera el servidor no le tenemos que indicar con un
        evento que ese es el nickname
        */
        // Ahora enviamos el nickname
        enviarObjeto(nickAEnviar);

        // Hilo que escucha los mensajes del servidor
        new Thread(() -> {
            try {
                // Se ignora el primer mensaje de bienvennida
                objectIn.readObject();

                while (true) {
                    Object objetoRecibido =  objectIn.readObject();
                    // Si objeto es un String (un mensaje de texto)
                    if (objetoRecibido instanceof String) {
                        String mensaje = (String) objetoRecibido;
                        System.out.println("Mensaje de texto recibido: " + mensaje);

                        if (mensaje.startsWith("PARTIDA_INICIADA:")) {
                            this.oponentePendiente = mensaje.substring("PARTIDA_INICIADA:".length()).trim();
                            System.out.println("Oponente '" + oponentePendiente + "' registrado. Esperando lista de personajes...");
                            // Si ya tenemos personajes pendientes, disparamos el listener ahora
                            // Esto cubre el caso donde los personajes llegan ANTES del mensaje de PARTIDA_INICIADA
                            if (this.personajesPendientes != null && this.clienteListener != null) {
                                partidaCliente = new Partida(); // Inicializamos la partida

                                // Mandamos a la base de datos el jugador 1
                                partidaCliente.setJugador1(Menu.nickName);

                                String oponenteFinal = this.oponentePendiente;
                                List<Personaje> persFinal = this.personajesPendientes;
                                Platform.runLater(() -> this.clienteListener.onIniciarPartida(oponenteFinal, persFinal));
                                this.oponentePendiente = null;
                                this.personajesPendientes = null;
                            }
                        } else if (mensaje.startsWith("RESULTADO:")) {
                            String[] partes = mensaje.split(":");
                            String tipoResultado = partes[1]; // GANASTE o PERDISTE
                            String nicknameGanador = partes[2];

                            Platform.runLater(() -> {
                                if (tipoResultado.equals("GANASTE")) {
                                    System.out.println("¡Felicidades! Has ganado la partida contra " + nicknameGanador);

                                    //Mandamos el nickname del ganador a la base de datos local
                                    partidaCliente.setWinner(nicknameGanador);

                                    // Notifica a tu UI que has ganado. Tu MensajeListener o un nuevo FinPartidaListener podría manejar esto.
                                    if (mensajeListener != null) {
                                        mensajeListener.onManejarMensajeServidor("HAS_GANADO:" + nicknameGanador);
                                    }
                                } else if (tipoResultado.equals("PERDISTE")) {
                                    System.out.println("Has perdido la partida. " + nicknameGanador + " ha ganado.");
                                    // Notifica a tu UI que has perdido.
                                    if (mensajeListener != null) {
                                        mensajeListener.onManejarMensajeServidor("HAS_PERDIDO:" + nicknameGanador);
                                    }
                                }
                                // Aquí podrías deshabilitar los controles de juego y preparar la UI para el final.
                            });
                        } else if (mensaje.startsWith("PERSONAJE_GANADOR_FINAL:")) {
                            int idPersonajeGanador = Integer.parseInt(mensaje.split(":")[1]);

                            //Mandamos el personaje ID a la base de datos local
                            partidaCliente.setPersonajeWinner(PersonajeDB.getPersonaje(idPersonajeGanador, false, false, false));
                        } else {
                            // Para cualquier otro mensaje de texto, lo pasamos al tablero.
                            if (this.mensajeListener != null) {
                                Platform.runLater(() -> this.mensajeListener.onManejarMensajeServidor(mensaje));
                            } else {
                                this.mensajePendiente = mensaje;
                            }
                        }
                    } else if (objetoRecibido instanceof List) {
                            @SuppressWarnings("unchecked")
                            List<Personaje> personajesRecibidos = (List<Personaje>) objetoRecibido;
                            System.out.println("Lista de personajes recibida con " + personajesRecibidos.size() + " personajes.");

                            // Iniciamos la partida ahora que tenemos la lista
                            if (this.oponentePendiente != null && this.clienteListener != null) {
                                // Iniciamos la partida
                                String oponenteFinal = this.oponentePendiente;
                                Platform.runLater(() -> this.clienteListener.onIniciarPartida(oponenteFinal, personajesRecibidos));
                                this.oponentePendiente = null; // Limpiamos para la siguiente partida.
                                this.personajesPendientes = null;
                            } else {
                                // Si el listener no está listo, guardamos la lista.
                                this.personajesPendientes = personajesRecibidos;
                            }
                    }
                }
            } catch (Exception e) {
                System.out.println("Se ha perdido la conexión con el servidor: " + e.getMessage());
            } finally {
                desconexion();
            }
        }).start();
    }

    //---------------------- Metodos para enviar mensajes al servidor (CON CONTROL DE TIPO) ----------------------

    // CAMBIO: Nuevo metodo central para enviar cualquier objeto de forma segura.
    public void enviarObjeto(Object objeto) {
        try {
            if (objectOut != null) {
                objectOut.writeObject(objeto);
                objectOut.flush(); // Asegura que el objeto se envíe inmediatamente.
            }
        } catch (IOException e) {
            System.err.println("Error al enviar objeto: " + e.getMessage());
        }
    }

    //Este metodo es el que usara el Tablero para enviar preguntas/respuestas/turnos
    public void enviarMensaje(String tipoMensaje, String mensaje) {
        enviarObjeto(tipoMensaje + ":" + mensaje);
    }

    // --------------------- VOLVER A JUGAR -----------------------
    // Para enviar mensajes desde cualquier lado simples al servidor
    public void enviarMensajesC(String mensaje) {
        enviarObjeto(mensaje);
    }

    // ------------- Métodos para configurar los Listeners desde la UI (sin cambios) -------
    public void setMensajeListener(MensajeListener listener) {
        this.mensajeListener = listener;
        if (mensajePendiente != null) {
            String mensajeADelante = mensajePendiente;
            mensajePendiente = null;
            Platform.runLater(() -> this.mensajeListener.onManejarMensajeServidor(mensajeADelante));
        }
    }

    // Establece un listener para eventos especiales (como iniciar la partida)
    public void setClienteListener(ClienteListener listener) {
        this.clienteListener = listener;

        // Si el oponente y personajes ya llegaron antes de que este listener se configurara, lo disparamos ahora
        if (this.oponentePendiente != null && this.personajesPendientes != null) {
            String oponenteFinal = this.oponentePendiente;
            List<Personaje> persFinal = this.personajesPendientes;
            Platform.runLater(() -> this.clienteListener.onIniciarPartida(oponenteFinal, persFinal));
            this.oponentePendiente = null;
            this.personajesPendientes = null;
        }
    }

    public void setPersonajeListener(PersonajesListener listener){
        this.personajesListener = listener;

        // Si hay personajes pendientes que llegaron antes de que este listener estuviera listo, los entregamos ahora
        if (personajesPendientes != null){
            List<Personaje> personajesADelante = personajesPendientes;
            personajesPendientes = null;
            Platform.runLater(() -> this.personajesListener.onPersonajesRecibidos(personajesADelante));
        }
    }

    // ------------------ Interfaces para los Listeners (sin cambios) ------------
    public interface MensajeListener { void onManejarMensajeServidor(String mensaje); }
    public interface ClienteListener { void onIniciarPartida(String oponenteNick, List <Personaje> personajesRecibidos);}
    public interface PersonajesListener { void onPersonajesRecibidos(List<Personaje> personajes); }

    // ----------- Desconexión -----------
    // Metodo para la desconexion del cliente
    public void desconexion() {
        try {
            // Cerramos los streams y el socket. Asignamos null para evitar usos posteriores.
            if (objectOut != null) {
                objectOut.close();
                objectOut = null;
            }
            if (objectIn != null) {
                objectIn.close();
                objectIn = null;
            }
            if (socket != null && !socket.isClosed()) {
                socket.close();
                socket = null;
            }
            // Limpiar los listeners y los datos pendientes al desconectar para evitar problemas
            mensajeListener = null;
            clienteListener = null;
            personajesListener = null;
            mensajePendiente = null;
            personajesPendientes = null;
            oponentePendiente = null;
            System.out.println("Cliente desconectado y recursos liberados.");
        } catch (IOException e) {
            System.out.println("Error al cerrar los recursos del cliente: " + e.getMessage());
        }
    }

    // Para verificar si el socket está cerrado desde fuera de la clase
    public boolean isClosed() {
        return socket == null || socket.isClosed();
    }
}