package Sockets;

import Classes.PaqueteInicioPartida; // <-- ¡IMPORTANTE!
import Classes.Partida;
import Classes.Personaje;
import DataBaseClasses.PartidaDB;
import Menu.Menu;
import javafx.application.Platform;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

public class Cliente {

    private Socket socket;
    private ObjectOutputStream objectOut;
    private ObjectInputStream objectIn;

    private MensajeListener mensajeListener;
    private ClienteListener clienteListener;

    // Almacén temporal para mensajes que llegan antes de que la UI esté lista.
    private String mensajePendiente = null;
    private String host;

    public Cliente(String host, int puerto, ClienteListener clienteListener) throws IOException {
        this.clienteListener = clienteListener;
        socket = new Socket(host, puerto);
        this.host = host;

        // Es crucial crear el Output stream DESPUÉS del servidor para evitar deadlocks.
        // Pero para que el cliente no se quede bloqueado, creamos el output primero y hacemos flush.
        this.objectOut = new ObjectOutputStream(socket.getOutputStream());
        this.objectOut.flush();
        this.objectIn = new ObjectInputStream(socket.getInputStream());

        String nickAEnviar = (Menu.nickName != null && !Menu.nickName.isEmpty())
                ? Menu.nickName.trim()
                : "Unknown Player";
        enviarObjeto(nickAEnviar);

        // Hilo que escucha los mensajes del servidor
        new Thread(this::escucharAlServidor).start();
    }

    private void escucharAlServidor() {
        try {
            objectIn.readObject(); // Ignoramos el mensaje de bienvenida.

            while (socket != null && !socket.isClosed()) {
                Object objetoRecibido = objectIn.readObject();

                // ----------- MANEJO DE OBJETOS SIMPLIFICADO -----------

                if (objetoRecibido instanceof PaqueteInicioPartida) {
                    PaqueteInicioPartida paquete = (PaqueteInicioPartida) objetoRecibido;
                    System.out.println("Paquete de inicio de partida recibido. Oponente: " + paquete.getOponenteNick());
                    if (this.clienteListener != null) {
                        Platform.runLater(() -> this.clienteListener.onIniciarPartida(paquete.getOponenteNick(), paquete.getPersonajes()));
                    }
                } else if (objetoRecibido instanceof String) {
                    String mensaje = (String) objetoRecibido;
                    System.out.println("Mensaje de texto recibido: " + mensaje);

                    // La lógica para RESULTADO es ahora más simple y directa
                    if (mensaje.startsWith("RESULTADO:")) {
                        // El formato es "RESULTADO:GANASTE:nick" o "RESULTADO:PERDISTE:nick"
                        // El TableroController decidirá qué hacer basado en si el mensaje contiene "GANASTE".
                        if (mensajeListener != null) {
                            Platform.runLater(() -> this.mensajeListener.onManejarMensajeServidor(mensaje));
                        } else {
                            this.mensajePendiente = mensaje;
                        }
                    } else {
                        // Para cualquier otro mensaje (TU_TURNO, PREGUNTA, etc.)
                        if (this.mensajeListener != null) {
                            Platform.runLater(() -> this.mensajeListener.onManejarMensajeServidor(mensaje));
                        } else {
                            this.mensajePendiente = mensaje;
                        }
                    }
                } else if (objetoRecibido instanceof Partida) {
                    Partida partida = (Partida) objetoRecibido;
                    System.out.println("Objeto Partida recibido para guardado local.");
                    System.out.println("Partida recibida: " + partida);
                    try {
                        if (!this.host.equals(("localhost")))
                            PartidaDB.insertarPartida(partida);
                        System.out.println("Partida insertada correctamente en la BD local.");
                    } catch (Exception e) {
                        System.out.println("Error al insertar la partida en la BD local.");
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Se ha perdido la conexión con el servidor: " + e.getMessage());
        } finally {
            desconexion();
        }
    }

    public void enviarObjeto(Object objeto) {
        try {
            if (objectOut != null) {
                objectOut.writeObject(objeto);
                objectOut.flush();
            }
        } catch (IOException e) {
            System.err.println("Error al enviar objeto: " + e.getMessage());
        }
    }

    public void enviarMensaje(String tipoMensaje, String mensaje) {
        enviarObjeto(tipoMensaje + ":" + mensaje);
    }

    public void setMensajeListener(MensajeListener listener) {
        this.mensajeListener = listener;
        if (mensajePendiente != null) {
            String mensajeADelante = mensajePendiente;
            this.mensajePendiente = null;
            Platform.runLater(() -> this.mensajeListener.onManejarMensajeServidor(mensajeADelante));
        }
    }

    // Ya no necesitas setPersonajesListener por separado
    // El clienteListener se encargará de pasar los personajes y el oponente a la vez.
    public void setClienteListener(ClienteListener listener) {
        this.clienteListener = listener;
    }


    public interface MensajeListener { void onManejarMensajeServidor(String mensaje); }
    public interface ClienteListener { void onIniciarPartida(String oponenteNick, List<Personaje> personajesRecibidos); }
    // La interfaz PersonajesListener ya no es necesaria

    public void desconexion() {
        try {
            if (objectOut != null) objectOut.close();
            if (objectIn != null) objectIn.close();
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException e) {
            System.out.println("Error al cerrar los recursos del cliente: " + e.getMessage());
        } finally {
            objectOut = null;
            objectIn = null;
            socket = null;
            mensajeListener = null;
            clienteListener = null;
            System.out.println("Cliente desconectado y recursos liberados.");
        }
    }

    public boolean isClosed() {
        return socket == null || socket.isClosed();
    }

}