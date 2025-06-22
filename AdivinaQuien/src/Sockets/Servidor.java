package Sockets;

import Classes.Partida;
import Classes.Personaje;
import DataBaseClasses.JugadorDB;
import DataBaseClasses.PartidaDB;
import DataBaseClasses.PersonajeDB;

import java.io.*;
import java.net.*;
import java.util.*;

// imports para las colas y los maps.
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/* Maneje la mayoria de las varibles que se van a mandar a otros package
   o clases como estaticas esto para no tener que instanciar tantos objetos*/

public class Servidor {
    // ---------------------- ATRIBUTOS ----------------------
    private static final int PUERTO = 5000;

    // -------- MATCHMAKING --------
    // Cola de Espera: Aquí se meten TODOS los jugadores que están listos para una partida.
    private static final Queue<Socket> colaDeEspera = new ConcurrentLinkedQueue<>();
    // Mapa de Parejas: Nos dice quién está jugando contra quién en este momento.
    private static final Map<Socket, Socket> oponentesEnPartida = new ConcurrentHashMap<>();

    // ----- Atributos de comunicación Cliente - Servidor - Cliente -----
    private static final Map<Socket, ObjectOutputStream> clienteOutStream = new ConcurrentHashMap<>();
    private static final Map<Socket, String> clientesNickName = new ConcurrentHashMap<>();

    // ----- Atributos del juego -----
    private static final Map<Socket, Socket> jugadorTurno = new  ConcurrentHashMap<>();
    private static final Map<Socket, Socket> jugadorEspResp = new ConcurrentHashMap<>();
    private static final Map<Socket, Integer> personajesSecretos = new ConcurrentHashMap<>();
    private static final Map<Socket, Boolean> estaEnPartida = new ConcurrentHashMap<>();

    // Partida Base Datos
    public static Partida partida;

    // ---------------------- MAIN ----------------------
    public static void main(String[] args) {
        try (ServerSocket server = new ServerSocket(PUERTO)) {
            System.out.println("Servidor Iniciado en el puerto: " + PUERTO);

            // Hilo vigilar la cola de espera y formar parejas.
            new Thread(() -> {
                while (true) {
                    try {
                        // Si hay 2 o más jugadores esperando
                        if (colaDeEspera.size() >= 2) {
                            Socket j1 = colaDeEspera.poll(); // Saca al primero
                            Socket j2 = colaDeEspera.poll(); // Saca al segundo

                            // Verificamos que ambos sockets sigan conectados y no estén ya en partida.
                            // Esto evita emparejar clientes que ya se desconectaron o que están en un estado intermedio
                            if (j1 != null && j2 != null && j1.isConnected() && j2.isConnected() &&
                                    !estaEnPartida.getOrDefault(j1, false) && !estaEnPartida.getOrDefault(j2, false)) {

                                estaEnPartida.put(j1, true); // Marcar como "en partida"
                                estaEnPartida.put(j2, true); // Marcar como "en partida"

                                // Informa al sistema que estos dos ahora son oponentes.
                                oponentesEnPartida.put(j1, j2);
                                oponentesEnPartida.put(j2, j1);

                                // Iniciamos la partida
                                iniciarNuevaPartida(j1, j2);
                            } else {
                                // Si uno de los jugadores no es válido, lo volvemos a poner en la cola
                                // o lo descartamos si está desconectado.
                                if (j1 != null && j1.isConnected() && !estaEnPartida.getOrDefault(j1, false)) colaDeEspera.add(j1);
                                if (j2 != null && j2.isConnected() && !estaEnPartida.getOrDefault(j2, false)) colaDeEspera.add(j2);
                                else if (j1 != null) { /* J1 no válido o desconectado, descartar */ }

                                if (j2 != null && j2.isConnected() && !estaEnPartida.getOrDefault(j2, false)) colaDeEspera.add(j2);
                                else if (j2 != null) { /* J2 no válido o desconectado, descartar */ }
                            }
                        }
                        Thread.sleep(1000); // Revisa la cola cada segundo.
                    } catch (InterruptedException e) { e.printStackTrace(); } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }).start();

            // El ciclo principal del servidor acepta las conexiones
            // y le pasa el trabajo al hilo 'manejarCliente'.
            while (true) {
                Socket cliente = server.accept();
                System.out.println("Nuevo cliente conectado: " + cliente.getInetAddress());
                new Thread(() -> manejarCliente(cliente)).start();
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    // Metodo que se encarga de recibir a los jugadores
    private static synchronized void iniciarNuevaPartida(Socket jugador1, Socket jugador2) throws IOException {
        System.out.println("\n\nServidor: Iniciando nueva partida para " + clientesNickName.get(jugador1) + " y " + clientesNickName.get(jugador2));

        partida = new Partida(); // Aqui inicializamos la partida por ocurren errores

        //Limpiamos los estados de partidas anteriores
        jugadorTurno.remove(jugador1);
        jugadorTurno.remove(jugador2);
        jugadorEspResp.remove(jugador1);
        jugadorEspResp.remove(jugador2);
        personajesSecretos.remove(jugador1);
        personajesSecretos.remove(jugador2);

        // Aqui obtenemos de manera alteatoria los personajes de la base de datos
        List<Personaje> personajes = PersonajeDB.generarTablero();
        if (personajes.isEmpty()){
            System.err.println("Warning: La lista de personajes generada esta vacia");
        }

        // Identificamos a los jugadores
        String nickJugador1 = JugadorDB.conectarse(clientesNickName.get(jugador1));
        String nickJugador2 = JugadorDB.conectarse(clientesNickName.get(jugador2));

        ObjectOutputStream oos1 = clienteOutStream.get(jugador1);
        ObjectOutputStream oos2 = clienteOutStream.get(jugador2);

        // Aqui mandamos el nickName del jugador1 y jugador2 a la base de datos
        partida.setJugador1(nickJugador1);
        partida.setJugador2(nickJugador2);


        // Reseteamos los streams para eliminar basura
        oos1.reset();
        oos2.reset();

        // Enviamos los mensajes a cada jugador
        System.out.println("\bCola iniciar: " + colaDeEspera.size() + "\n");
        oos1.writeObject("PARTIDA_INICIADA:" + nickJugador2);
        oos2.writeObject("PARTIDA_INICIADA:" + nickJugador1);

        oos1.writeObject(new ArrayList<>(personajes));
        oos2.writeObject(new ArrayList<>(personajes));
    }

    // Metodo para pasar de Sala de Espera a la Partida y De la partida A Sala de Espera en caso de volver a jugar
    private static void manejarCliente(Socket cliente) {
        try {
            // Configuración de la conexión
            ObjectOutputStream oos = new ObjectOutputStream(cliente.getOutputStream());
            oos.flush();
            clienteOutStream.put(cliente, oos);

            ObjectInputStream ois = new ObjectInputStream(cliente.getInputStream());


            String nickJugador = (String) ois.readObject();
            if (nickJugador == null || nickJugador.trim().isEmpty()) { return; }
            clientesNickName.put(cliente, nickJugador);
            System.out.println("Nickname recibido: " + nickJugador);

            oos.writeObject("Servidor: Bienvenido, " + nickJugador + " Buscando partida...");

            // Inicializamos el estado de partida como falso al conectarse
            estaEnPartida.put(cliente, false);

            // Este ciclo se asegura de que un jugador pueda jugar múltiples partidas.
            while (cliente.isConnected()) {
                System.out.println("\nCola 2: " + colaDeEspera.size() + "\n");
                // LOBBY: El jugador entra a la cola y espera.
                // Verificamos si el cliente se encuentra en la partida o no
                if (!estaEnPartida.getOrDefault(cliente, false) && !colaDeEspera.contains(cliente) ) {
                    colaDeEspera.add(cliente);
                    System.out.println(nickJugador + " ha entrado en la cola de espera.");
                }

                // El hilo se queda aquí, esperando a que el Matchmaker le asigne un oponente.
                while (!estaEnPartida.getOrDefault(cliente, false)) {
                    if (!cliente.isConnected()) return; // Sale si se desconecta esperando.
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {}
                }

                Socket oponente = oponentesEnPartida.get(cliente);
                if (oponente == null || !oponente.isConnected()) {
                    System.out.println(clientesNickName.get(cliente) + ": Oponente no encontrado o desconectado. Volviendo a la cola.");
                    estaEnPartida.put(cliente, false); // Permitir que se reencole
                    oponentesEnPartida.remove(cliente); // Asegurar que no quede como emparejado
                    continue; // Vuelve al inicio del bucle para reevaluar la situación
                }

                System.out.println(nickJugador + " fue emparejado con " + clientesNickName.get(oponente));

                // PARTIDA: Entra en al ciclo de juego.
                Object mensajeObj;
                while (cliente.isConnected() && oponente.isConnected() && estaEnPartida.getOrDefault(cliente, false)) {
                    try {
                        mensajeObj = ois.readObject();
                    } catch (IOException e) {
                        System.out.println("Cliente " + clientesNickName.get(cliente) + " cerró el stream de entrada. Desconectando.");
                        break; // Sale del ciclo de partida si el stream se cerró
                    }

                    if (!(mensajeObj instanceof String)) continue; // Ignoramos si no es un String
                    String mensaje = (String) mensajeObj;

                    System.out.println("Mensaje de " + nickJugador + ": " + mensaje);

                    if (mensaje.equals("JUGAR_OTRA_VEZ")) {
                        // Limpiamos la relación de oponentes y el estado "en partida"
                        oponentesEnPartida.remove(oponente);
                        estaEnPartida.put(cliente, false); // Marca al cliente como NO en partida
                        estaEnPartida.put(oponente, false); // Marca al oponente como NO en partida

                        // Limpiar también los estados específicos de la partida para ambos
                        jugadorTurno.remove(cliente);
                        jugadorTurno.remove(oponente);
                        jugadorEspResp.remove(cliente);
                        jugadorEspResp.remove(oponente);
                        personajesSecretos.remove(cliente);
                        personajesSecretos.remove(oponente);

                        System.out.println(clientesNickName.get(cliente) + " quiere jugar otra vez. Volviendo a la cola.");

                        if (!colaDeEspera.contains(cliente)) {
                            colaDeEspera.add(cliente);
                        }

                        System.out.println("\nCola 3: " + colaDeEspera.size() + "\n");
                        break; // Rompe el ciclo de la partida para volver al lobby
                    }

                    if (mensaje.startsWith("PREGUNTA:")) {
                        if (cliente.equals(jugadorTurno.get(cliente))) { // Si el cliente es el que tiene el turno en SU partida
                            String pregunta = mensaje.substring("PREGUNTA:".length()).trim();
                            clienteOutStream.get(oponente).writeObject("PREGUNTA:" + clientesNickName.get(cliente) + ":" + pregunta);
                            jugadorEspResp.put(cliente, oponente); // Este cliente espera la respuesta de SU oponente
                        } else {
                            oos.writeObject("ERROR: No es tu turno para preguntar");
                        }
                    } else if (mensaje.startsWith("RESPUESTA:")) {
                        // Si NO es mi turno Y mi oponente me está esperando una respuesta
                        if (!cliente.equals(jugadorTurno.get(cliente)) && jugadorEspResp.containsKey(oponente) && jugadorEspResp.get(oponente).equals(cliente)) {
                            String respuesta = mensaje.substring("RESPUESTA:".length()).trim();
                            Socket jugadorPregunton = oponente; // El que preguntó es el oponente en ESTA PARTIDA
                            clienteOutStream.get(jugadorPregunton).writeObject("RESPUESTA:" + clientesNickName.get(cliente) + ":" + respuesta);
                            jugadorEspResp.remove(oponente); // Limpiamos el estado de espera para ESE oponente
                            Thread.sleep(500);
                            cambiarTurno(jugadorPregunton, cliente); // Cambiar turno del que preguntó al que respondió
                        } else {
                            oos.writeObject("ERROR: No puedes responder en este momento");
                        }
                    } else if (mensaje.startsWith("ADIVINAR:")) {
                        int idAdivinado = Integer.parseInt(mensaje.substring("ADIVINAR:".length()).trim());
                        int idPersonajeSecretoOP = personajesSecretos.getOrDefault(oponente, -1);
                        String ganadorNick;
                        Socket ganadorSocket;
                        //Socket perdedorSocket;
                        int idPersonajeGanadorSecreto; // Esta variable almacenará el ID del personaje secreto del GANADOR REAL

                        if (idAdivinado == idPersonajeSecretoOP) {
                            ganadorNick = nickJugador; // El jugador actual es el ganador
                            //ganadorSocket = cliente;
                            //perdedorSocket = oponente;
                            idPersonajeGanadorSecreto = idPersonajeSecretoOP; // El personaje adivinado era el secreto del ganador
                            oos.writeObject("RESULTADO:GANASTE:" + ganadorNick);
                            clienteOutStream.get(oponente).writeObject("RESULTADO:PERDISTE:" + ganadorNick); // Notifica al oponente que perdió
                        } else {
                            ganadorNick = clientesNickName.get(oponente); // El oponente es el ganador
                            ganadorSocket = oponente;
//                            perdedorSocket = cliente;
                            idPersonajeGanadorSecreto = personajesSecretos.getOrDefault(ganadorSocket, -1); // Obtiene el personaje secreto del oponente
                            oos.writeObject("RESULTADO:PERDISTE:" + ganadorNick); // Notifica al jugador actual que perdió
                            clienteOutStream.get(oponente).writeObject("RESULTADO:GANASTE:" + ganadorNick);
                        }

                        // Actualiza el objeto Partida para la base de datos
                        partida.setWinner(ganadorNick);
                        // Asegúrate de recuperar el objeto Personaje basado en el ID secreto del ganador
                        partida.setPersonajeWinner(PersonajeDB.getPersonaje(idPersonajeGanadorSecreto, false, false, false));


                        try {
                            // Envía el ID del personaje ganador real a ambos jugadores
                            clienteOutStream.get(cliente).writeObject("PERSONAJE_GANADOR_FINAL:" + idPersonajeGanadorSecreto);
                            System.out.println("Enviando ID del personaje ganador (" + idPersonajeGanadorSecreto + ") a " + nickJugador);

                            clienteOutStream.get(oponente).writeObject("PERSONAJE_GANADOR_FINAL:" + idPersonajeGanadorSecreto);
                            System.out.println("Enviando ID del personaje ganador (" + idPersonajeGanadorSecreto + ") a " + clientesNickName.get(oponente));

                        } catch (Exception e) {
                            System.err.println("Error al enviar el ID del personaje ganador final: " + e.getMessage());
                        }

                        System.out.println("Partida terminada. " + nickJugador + " y " + clientesNickName.get(oponente) + " son libres xd");
                        System.out.println("\nCola 1: " + colaDeEspera.size() + "\n");

                        // Mandamos la partida a base de datos
                        PartidaDB.insertarPartida(partida);

                        break; // Rompe el ciclo de la partida.
                    } else if (mensaje.startsWith("PERSONAJE_ELEGIDO:")) {
                        // Sincronizar al usar el mapa de personajes secretos y verificar que ambos están listos
                        synchronized (personajesSecretos) { // Sincronizamos en el mapa para evitar problemas al escribir
                            int idPersonajeElegido = Integer.parseInt(mensaje.substring("PERSONAJE_ELEGIDO:".length()).trim());
                            personajesSecretos.put(cliente, idPersonajeElegido);

                            // Si ambos jugadores ya eligieron personaje, iniciar el turno
                            if (personajesSecretos.containsKey(cliente) && personajesSecretos.containsKey(oponente)) {
                                System.out.println("Servidor: Ambos jugadores listos. Asignando turno inicial.");

                                asignarTurnoInicial(cliente, oponente); // Pasar los sockets de la partida actual

                                oos.writeObject("INICIAR_CRONOMETRO");
                                clienteOutStream.get(oponente).writeObject("INICIAR_CRONOMETRO");
                            }
                        }
                    } else if (mensaje.equals("TURNO_TERMINADO")) {
                        // Si es el turno de ese cliente le pasamos el turno
                        if (cliente.equals(jugadorTurno.get(cliente))) { // Si el cliente es el que tiene el turno
                            cambiarTurno(cliente, oponente); // Pasar los sockets
                        } else {
                            oos.writeObject("ERROR: No puedes terminar el turno si no es tuyo");
                        }
                    }
                } // Aqui termina
            }
        } catch (Exception e) {
            System.out.println("Conexión perdida o error con cliente. " + e.getMessage());
        } finally {
            // Hacemos limpieza general
            String nick = clientesNickName.getOrDefault(cliente, "un cliente desconocido");
            System.out.println("Limpiando datos del jugador desconectado: " + nick);
            colaDeEspera.remove(cliente);
            Socket oponente = oponentesEnPartida.remove(cliente);
            if (oponente != null) {
                oponentesEnPartida.remove(oponente);

                // Marcar a ambos como no en partida, vital para re-emparejamiento
                estaEnPartida.put(cliente, false);
                estaEnPartida.put(oponente, false);

                // Limpiar también los estados específicos de la partida para ambos
                jugadorTurno.remove(cliente);
                jugadorTurno.remove(oponente);
                jugadorEspResp.remove(cliente);
                jugadorEspResp.remove(oponente);
                personajesSecretos.remove(cliente);
                personajesSecretos.remove(oponente);

                if(clienteOutStream.containsKey(oponente)) {
                    try {
                        clienteOutStream.get(oponente).writeObject("OPONENTE_DESCONECTADO:" + nick);
                        if (oponente.isConnected() && !colaDeEspera.contains(oponente)) {
                            colaDeEspera.add(oponente);
                            System.out.println(clientesNickName.get(oponente) + " reencolado por desconexión de oponente.");
                        }
                    } catch (IOException ioException) {
                        System.err.println("No se pudo notificar al oponente de la desconexión.");
                    }
                }
            } else {
                // Si el cliente no estaba en una partida, solo asegúrate de que no esté en la cola
                estaEnPartida.put(cliente, false);
            }
            clienteOutStream.remove(cliente);
            clientesNickName.remove(cliente);
            try {
                if (cliente != null && !cliente.isClosed()) cliente.close();
            } catch (IOException ex) { ex.printStackTrace(); }
        }
    }

    // Metodo para asignar el turno inicial a quien corresponda
    private static void asignarTurnoInicial(Socket j1, Socket j2){
        try {
            Socket primerTurno = new Random().nextBoolean() ? j1 : j2;
            String nickTurno = clientesNickName.get(primerTurno);

            // Ahora asignamos quién tiene el turno en el mapa, para esta partida.
            jugadorTurno.put(j1, primerTurno); // Ambos saben quién tiene el turno
            jugadorTurno.put(j2, primerTurno);

            System.out.println("Turno asignado a: " + nickTurno);
            clienteOutStream.get(j1).writeObject("TU_TURNO:" + nickTurno);
            clienteOutStream.get(j2).writeObject("TU_TURNO:" + nickTurno);
        } catch (IOException e) { e.printStackTrace(); }
    }

    // Metodo para cambiar de turno
    public static void cambiarTurno(Socket jugadorActual, Socket oponente){
        try {
            // Actualizamos el mapa para ambos jugadores.
            jugadorTurno.put(jugadorActual, oponente); // El actual ahora sabe que el oponente tiene el turno
            jugadorTurno.put(oponente, oponente); // El oponente ahora sabe que TIENE el turno

            String nickTurno = clientesNickName.get(oponente);
            System.out.println("El turno cambió a: " + nickTurno);
            clienteOutStream.get(jugadorActual).writeObject("TU_TURNO:" + nickTurno);
            clienteOutStream.get(oponente).writeObject("TU_TURNO:" + nickTurno);
        } catch (IOException e) { e.printStackTrace(); }
    }
}