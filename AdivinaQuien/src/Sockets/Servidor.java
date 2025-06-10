package Sockets;

// Librerias principales a utilizar
import Classes.Personaje;
import DataBaseClasses.PersonajeDB;

import java.io.*;
import java.net.*;
import java.util.*;

/* Maneje la mayoria de las varibles que se van a mandar a otros package
   o clases como estaticas esto para no tener que instanciar tantos objetos*/

public class Servidor {
    // ---------------------- ATRIBUTOS ----------------------
    // Asignamos un puerto al cual accedera nuestro servidor
    private static final int PUERTO = 5000;

    // Para los ObjectOutputStream usaremos maps
    private static Map<Socket, ObjectOutputStream> clienteOutStream = new HashMap<>();


    // Para los nicknames y las salidas a cada cliente (PrintWriter) usaremos map
    private static Map<Socket, PrintWriter> clientesOut = new HashMap<>(); // Para las salidas hacia los clientes
    private static Map<Socket, String> clientesNickName = new HashMap<>(); // Para los nicknames
    private static List<Socket> clientes = new ArrayList<>(); //Lista para los jugadores y el orden de la conexion
    private static Socket jugadorTurno = null; // Este socket determinara el jugador que tiene el turno
    private static Socket jugadorEspResp = null; // Este socket manejara al jugador que realizo la pregunta

    // Lista para los personajes
    private static List<Personaje> personajes = new ArrayList<>();

    public static void main(String[] args) {
        // Creamos el Server donde se escucharan y saldran los clientes
        // El bloque try - catch es para verificar que se cierre si algo falla
        try (ServerSocket server = new ServerSocket(PUERTO)) {
            System.out.println("Servidor Iniciado en el puerto: " + PUERTO + "\n");

            while (clientes.size() < 2) {
                Socket cliente = server.accept();
                clientes.add(cliente);
                System.out.println("Jugador conectado: " + cliente.getInetAddress());

                // Inicializamos ObjectOutputStream antes de PrintWriter para que no ocurrarn errores
                ObjectOutputStream clientOutput = new ObjectOutputStream(cliente.getOutputStream());
                clienteOutStream.put(cliente, clientOutput);

                // PrintWriter para el cliente
                PrintWriter out = new PrintWriter(new OutputStreamWriter(clientOutput), true);
                clientesOut.put(cliente, out); // Se añade al map

                // Hilo para manejar al cliente
                new Thread(() -> manejarCliente(cliente)).start();
            }

            // Enviamos el mensaje de que los 2 jugadores estan conectados a cada jugador
            if (clientes.size() == 2) {
                // Aqui obtenemos de manera alteatoria los personajes de la base de datos
                personajes = PersonajeDB.generarTablero();
                if (personajes.isEmpty()){
                    System.err.println("Warning: La lista de personajes generada esta vacia");
                }
                for (Socket client : clientes) {
                    clientesOut.get(client).println("LOS JUGADORES SE HAN CONECTADO");
                    System.out.println("Servidor: Mensaje 'LOS JUGADORES SE HAN CONECTADO' enviado a " + clientesNickName.get(client));

                    try {
                        ObjectOutputStream os = clienteOutStream.get(client); // Obtener el ObjectOutputStream del cliente
                        if (os != null) {
                            os.writeObject(personajes); // ¡Enviar la lista de objetos por el socket!
                            os.flush(); // Asegurar que los datos se envíen inmediatamente
                            System.out.println("Servidor: Lista de " + personajes.size() + " personajes enviada a " + clientesNickName.get(client));
                        } else {
                            System.err.println("Error: ObjectOutputStream para " + clientesNickName.get(client) + " es nulo.");
                        }
                    } catch (IOException e) {
                        System.err.println("Servidor: Error al enviar personajes a " + clientesNickName.get(client) + ": " + e.getMessage());
                        e.printStackTrace();
                    }

                }
                //Asignamos el turno inicial
                asignarTurnoInicial();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void manejarCliente(Socket cliente) {
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
            String mensaje;
            String nickJugador = null;

            System.out.println("\n------------------ NICKNAMES ------------------"); // Salto de linea
            // Leemos el nickname del jugador
            // Condicion la cual recibira el nickname, por que el primer mensaje que enviara el cliente sera ese
            if ((mensaje = in.readLine()) != null){
                nickJugador = mensaje;
                clientesNickName.put(cliente, nickJugador);
                System.out.println("Nickname recibido de " + cliente.getInetAddress() + ": " + nickJugador);
            }


            System.out.println("\n------------------ EVENTOS ------------------"); // Salto de linea
            // Cliclo que mientras haya algo en mensaje se repetira
            while ((mensaje = in.readLine()) != null) {
                System.out.println("Mensaje recibido de " + clientesNickName.get(cliente) + ": " + mensaje);

                // ---------------- MANEJO DE TURNOS Y MENSAJES ----------------

                // FORMATO QUE USARA EL SERVIDOR PARA IDENTIFICAR SI ES PREGUNTA, RESPUESTA O EL NICKNAME DEL JUGADOR
                // "TIPO_MENSAJE:MENSAJE"
                // Tipos de mensajes: "NICKNAME", "PREGUNTA" y "RESPUESTA"

                if (mensaje.startsWith("PREGUNTA:")){
                    if (cliente.equals(jugadorTurno)) { // Solo el jugador que tiene el turno puede contestar
                        // Obtenemos la pregunta
                        String pregunta = mensaje.substring("PREGUNTA:".length()).trim();
                        System.out.println("Pregunta de " + clientesNickName.get(cliente) + ": " + pregunta);

                        // Se la reenviamos al otro jugador
                        for (Socket client:  clientes) {
                            if (!client.equals(cliente)){
                                // PREGUNTA:NickName:pregunta
                                clientesOut.get(client).println("PREGUNTA:" + clientesNickName.get(cliente) + ":" + pregunta);
                                jugadorEspResp = cliente; // Almacenamos en una variable el jugador que envio la pregunta
                                System.out.println("\nEnviando pregunta a " + clientesNickName.get(client));
                                break;
                            }
                        }
                    } else {
                        clientesOut.get(cliente).println("ERROR: No es tu turno para preguntar");
                        System.out.println("ERROR: " + clientesNickName.get(cliente) + " intento preguntar fuera de su turno");
                    }
                } else if (mensaje.startsWith("RESPUESTA:")) {
                    // Esta condicion se asegura que solo el jugador que NO tiene el turno y NO hizo la pregunta pueda responder
                    if (!cliente.equals(jugadorTurno) && jugadorEspResp != null) {
                        // Obtenemos la respuesta
                        String repuesta = mensaje.substring("RESPUESTA:".length()).trim();
                        System.out.println("Respuesta de " + clientesNickName.get(cliente) + ": " + repuesta);

                        // Se la reenviamos respuesta al jugador que hizo la pregunta
                        clientesOut.get(jugadorEspResp).println("RESPUESTA:" + clientesNickName.get(cliente) + ":" + repuesta);
                        System.out.println("Enviando respuesta a " + clientesNickName.get(jugadorEspResp));
                        jugadorEspResp = null; // Limpiamos despues de enviar la respuesta

                        /* Una vez que respondio y se reenvio la respuesta, el turno vuelve al que pregunto.
                        Cabe aclarar que el turno se manejara en el Tablero.
                        */
                    } else {
                        clientesOut.get(cliente).println("ERROR: No puedes responder en este momento");
                        System.out.println("ERROR: " + clientesNickName.get(cliente) + " intento responder");
                    }
                } else if (mensaje.equals("TURNO TERMINADO")) {
                    // Mensaje del jugador que tiene el turno para indicar que termino su turno
                    if (cliente.equals(jugadorTurno)) {
                        cambiarTurno();
                        System.out.println("Turno terminado por " + clientesNickName.get(cliente));
                    }else {
                        clientesOut.get(cliente).println("ERROR:No puedes terminar el turno si no es tuyo XD");
                        System.out.println("ERROR: " + clientesNickName.get(cliente) + " intentó terminar cuando no debia");
                    }
                // Si llega otro mensaje de tipo no definido, se ignora
                } else {
                    System.out.println("Mensaje desconocido de " + clientesNickName.get(cliente) + ": " + mensaje);
                }
            } // Fin while

            // ---------------- MANEJO DE DESCONEXION ----------------
            // En caso de que el cliente se desconecte
            System.out.println("Jugador desconectado: " + clientesNickName.get(cliente) + " (" + cliente.getInetAddress() + ")");
        } catch (IOException e) {
            // En caso de que se desconecte el cliente inesperadamente
            System.out.println("Error con el cliente " + clientesNickName.get(cliente) + ": " + e.getMessage());
        } finally {
            // Cuando el cliente se desconecta, se quita de la lista y se le notifica al otro que se desconecto
            // Quitamos de las listas
            String clienteDesconectado = clientesNickName.remove(cliente);
            clientes.remove(cliente);
            clientesOut.remove(cliente);
            clienteOutStream.remove(cliente);

            //Cerramos el cliente
            try {
                cliente.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Notificamos que el jugador oponente se desconecto
            for (Socket client : clientes) {
                try {
                    PrintWriter out = clientesOut.get(client);
                    if (out != null) {
                        out.println("OPONENTE DESCONECTADO:" + clienteDesconectado);
                        System.out.println("Se ha notificado a " + clientesNickName.get(client) + " que " + clienteDesconectado + " se desconecto\n");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            // Limpiamos el jugador turno, si se desconecta
            if (cliente.equals(jugadorTurno)) {
                jugadorTurno = null;
            }

            // Limpiamos el jugador que espera la respuesta, si se desconecta
            if (cliente.equals(jugadorEspResp)) {
                jugadorEspResp = null;
            }
        } // Fin try-catch-finally
    } // Fin manejarCliente

    // Metodo para asignar turno inicial
    private static void asignarTurnoInicial(){
        // Si ya hay 2 jugadores conectados asignamos el turno
        if (clientes.size() == 2){
            Random rand = new Random();
            int index = rand.nextInt(2); // 0 o 1
            jugadorTurno = clientes.get(index); // Asignamos el turno al jugador que salio
            String nickTurno = clientesNickName.get(jugadorTurno); // Obtenemos el nickname del jugador
            System.out.println("Turno asignado a: " + nickTurno + "\n");

            // Notificamos a los jugadores de quien tiene el turno
            for (Socket client : clientes) {
                String mensajeTurno = "TU_TURNO:" + nickTurno;
                clientesOut.get(client).println(mensajeTurno);
            }
        }
    }

    //Metodo para cambiar de turno
    private static void cambiarTurno(){
        // Si ya hay 2 jugadores conectados asignamos el turno
        if (clientes.size() == 2){
            // Buscamos al otro jugador
            for (Socket client : clientes) {
                // Si el jugador es el que no tiene el turno
                if (!client.equals(jugadorTurno)) {
                    jugadorTurno = client; // Asignamos el turno a ese jugador
                    break;
                }
            }
            String nickTurno = clientesNickName.get(jugadorTurno);
            System.out.println("El tuno cambio a: " + nickTurno + "\n");

            // Notificamos de quien tiene el turno
            for (Socket client : clientes) {
                String mensajeTurno = "TU_TURNO:" + nickTurno;
                clientesOut.get(client).println(mensajeTurno);
            }
        }
    }
}