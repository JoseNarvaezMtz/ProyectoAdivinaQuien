package Sockets;

// Librerias principales a utilizar
import javafx.application.Platform;
import Menu.Menu; // Importamos la clase menu para poder acceder al nick name
import Classes.Personaje; // Importamos la clase Personaje

import java.io.*;
import java.net.*;
import java.util.*;

public class Cliente {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private ObjectInputStream objectIn; // Para recibir objetos

    // Listener para mensajes generales
    private MensajeListener mensajeListener;

    // Listener para eventos (iniciar partida)
    private ClienteListener clienteListener;

    // Listener para los personajes poder enviarlos al tablero
    private PersonajesListener personajesListener;

    // Guaradar el ultimo mensaje importante y la lista de personajes que llegó antes de que el listener estuviera listo
    private String mensajePendiente = null;
    private List <Personaje> personajesPendientes = null;

    // Constructor: recibe el host, puerto y un listener para eventos
    public Cliente(String host, int puerto, ClienteListener clienteListener) throws IOException {
        this.clienteListener = clienteListener;
        socket = new Socket(host, puerto);

        // Inicializamos el ObjectInputStream
        InputStream ios = socket.getInputStream();
        objectIn = new  ObjectInputStream(ios);

        // Inicializamos el BufferReader con el ObjectInputStream
        in = new BufferedReader(new InputStreamReader(objectIn));

        // Inicializamos el PrintWriter para mandar mensjaes al servidor
        out = new PrintWriter(socket.getOutputStream(), true);

        // ----------------- Enviamos el nickname al servidor despues de conenctar -----------------
        // Esto por que es lo primero que espera el servidor
        // Si el nickname no es nulo y si en nickname no esta vacion se lo enviaremos al servidor
        if (Menu.nickName != null && !Menu.nickName.isEmpty()) {
            /* Se usa un metodo aparte para optimizar el codigo dicho de otra manera lo hacemos
            por que como es lo primero que espera el servidor no le tenemos que indicar con un
            evento que ese es el nickname
            */
            enviarMensajeNick(Menu.nickName.trim());
        } else {
            System.err.println("Warning xd: El nickname no esta establecido en la clase Menu.");
            enviarMensajeNick("Unknown Player"); // Enviamos un nickname por defecto
        }

        // Hilo que escucha los mensajes del servidor
        new Thread(() -> {
            String mensaje;
            try {
                mensaje = in.readLine(); // Leemos el mensaje
                System.out.println("Cliente: Mensaje Inicial es: " + mensaje);

                // Si el mensaje es "LOS JUGADORES SE HAN CONECTADO"
                if (mensaje != null && mensaje.startsWith("PARTIDA_INICIADA:")) {
                    // Extraemos el nickname del oponente del mensaje
                    String oponenteNick = mensaje.substring("PARTIDA_INICIADA:".length()).trim();
                    System.out.println("Cliente: Partida iniciada. Oponente: " + oponenteNick);

                    if (clienteListener != null) {
                        // Llamamos al listener y le pasamos el nickname
                        Platform.runLater(() -> clienteListener.onIniciarPartida(oponenteNick));
                    }

                    // Aqui leemos la lista antes de leer los mensajes para que se lo primero que obtiene el cliente
                    System.out.println("Cliente: Esperando lista de personajes del servidor...");
                    Object ObjRecibido = objectIn.readObject(); // Esperamos a que un objeto sea recibido

                    // Condicion que verifica si el objeto recibido es la lista de personajes
                    if (ObjRecibido instanceof List) {
                        List<Personaje> personajesRecibidos = (List<Personaje>) ObjRecibido;
                        System.out.println("Cliente: Lista de personajes recibida con " + personajesRecibidos.size() + " personajes.");

                        // Aqui es donde le decimos al tablero que la lista ya la recibio
                        if (personajesListener != null){
                            Platform.runLater(() -> personajesListener.onPersonajesRecibidos(personajesRecibidos));
                        } else {
                            // Si el listener dentro del tablero aun no esta configurado
                            // Guardamos la lista para entregarla despues
                            personajesPendientes = personajesRecibidos;
                            System.out.println("Cliente: Personajes recibidos pero el listener no se ha configurado");
                        }
                    } else {
                        System.out.println("Cliente: El objeto recibido no es una lista");
                    }
                } else {
                    // Si el primer mensaje no es ese
                    System.out.println("Cliente: Mensaje Inicial Distinto es: " + mensaje);
                    // Mensaje de error
                    if (mensajeListener != null) {
                        String finalMensaje = mensaje;
                        Platform.runLater(() -> mensajeListener.onManejarMensajeServidor(finalMensaje));
                    } else {
                        mensajePendiente = mensaje;
                    }
                }

                // Una vez que ya se recibio la lista, leemos los demas mensajes
                while((mensaje = in.readLine()) != null) {
                    System.out.println("Mensaje recibido: " + mensaje);

                    // Estos son los mensajes que interesan al TableroController
                    if (mensaje.startsWith("OPONENTE DESCONECTADO:") ||
                            mensaje.startsWith("TU_TURNO:") ||
                            mensaje.startsWith("PREGUNTA:") ||
                            mensaje.startsWith("RESPUESTA:") ||
                            mensaje.startsWith("INICIAR_CRONOMETRO") ||
                            mensaje.equals("TURNO TERMINADO") ||
                            mensaje.startsWith("ERROR:")) {

                        // Si hay un listener activo (el TableroController ya se configuró)
                        if (mensajeListener != null) {
                            String finalMensaje = mensaje;
                            Platform.runLater(() -> mensajeListener.onManejarMensajeServidor(finalMensaje));
                        } else {
                            // Si el listener aún NO está configurado, guarda el mensaje
                            mensajePendiente = mensaje;
                        }
                    } else {
                        // Para cualquier otro mensaje no clasificado, si hay listener, se lo pasamos
                        if (mensajeListener != null) {
                            String finalMensaje = mensaje;
                            Platform.runLater(() -> mensajeListener.onManejarMensajeServidor(finalMensaje));
                        }
                    }
                }
            } catch (EOFException e){
                System.out.println("Cliente: El servidor cerro la conexion");
            } catch (SocketException e){
                System.out.println("Cliente: Conexion con el servidor cerrada o reiniciada: " + e.getMessage());
            } catch (IOException e) {
                System.out.println("Error en el cliente (conexion cerrada): " + e.getMessage());

                // Si es por cierre de cliente no mostramos el printStackTrace por que no es necesario ya que mostramos el error
                if (socket != null && !socket.isClosed()) e.printStackTrace();
            } catch (ClassNotFoundException e){
                System.out.println("Error de deserializacion: Clase Personaje no encontrada: " + e.getMessage());
                e.printStackTrace();
            } finally {
                // Nos aseguramos de cerrar todo
                desconexion();

            }
        }).start();
    }

    //---------------------- Metodos para enviar mensajes al servidor (CON CONTROL DE TIPO) ----------------------

    //Este metodo es el que usara el Tablero para enviar preguntas/respuestas/turnos
    public void enviarMensaje(String tipoMensaje, String mensaje) {
        if (out != null) {
            out.println(tipoMensaje + ":"  + mensaje);
        }
    }

    // Metodo para enviar el nickname al servidor
    public void enviarMensajeNick(String nickName) {
        if (out != null) {
            out.println(nickName);
        }
    }

    //----------------------------- Metodos Set para establecer los listener -----------------------------
    // Metodo para estableceer el listener de mensajes normales y pendientes
    public void setMensajeListener(MensajeListener listener) {
        this.mensajeListener = listener;
        if (mensajePendiente != null) { // Si hay un mensaje que llegó antes de que el listener se configurara
            String mensajeADelante = mensajePendiente; // Copia el mensaje antes de limpiarlo
            mensajePendiente = null; // Limpia la variable después de entregarlo
            Platform.runLater(() -> this.mensajeListener.onManejarMensajeServidor(mensajeADelante));
        }
    }

    // Establece un listener para eventos especiales (como iniciar la partida)
    public void setClienteListener(ClienteListener listener) {
        this.clienteListener = listener;
    }

    public void setPersonajeListener(PersonajesListener listener){
        this.personajesListener = listener;
        if (personajesPendientes != null){ // Si hay personajes que llegaron antes de que se configurara el Listener
            List<Personaje> personajesADelante = personajesPendientes; // Copiamos la lista
            personajesPendientes = null; // Limpiamos la variable
            Platform.runLater(() -> this.personajesListener.onPersonajesRecibidos(personajesADelante));
        }
    }

    //----------------------------- Interfaces para recibir mensajes -----------------------------

    // Interfaz para recibir mensajes comunes
    public interface MensajeListener {
        void onManejarMensajeServidor(String mensaje);
    }

    // Interfaz para recibir los mensajes de eventos (Iniciar la partida)
    public interface ClienteListener{
        void onIniciarPartida(String oponenteNick);
    }

    public interface PersonajesListener{
        void onPersonajesRecibidos(List<Personaje> personajes);
    }

    // ------------------------ DESCONEXION ------------------------

    // Metodo para la desconexion del cliente
    public void desconexion() {
        try {
            if (objectIn != null) objectIn.close();
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null && !socket.isClosed()) socket.close();
        }catch (IOException e){
            System.out.println("Error al cerrar el y los recursos del cliente: " + e.getMessage());
        }
    }
}