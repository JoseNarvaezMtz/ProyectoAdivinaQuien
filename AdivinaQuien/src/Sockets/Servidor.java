package Sockets;

import Classes.PaqueteInicioPartida; // <-- ¡IMPORTANTE!
import Classes.Partida;
import Classes.Personaje;
import DataBaseClasses.JugadorDB;
import DataBaseClasses.PartidaDB;
import DataBaseClasses.PersonajeDB;

import java.io.*;
import java.net.*;
import java.time.Duration;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Servidor {

    private static final int PUERTO = 5000;
    private static final Queue<Socket> colaDeEspera = new ConcurrentLinkedQueue<>();
    private static final Map<Socket, Socket> oponentesEnPartida = new ConcurrentHashMap<>();
    private static final Map<Socket, ObjectOutputStream> clienteOutStream = new ConcurrentHashMap<>();
    private static final Map<Socket, String> clientesNickName = new ConcurrentHashMap<>();
    private static final Map<Socket, Socket> jugadorTurno = new ConcurrentHashMap<>();
    private static final Map<Socket, Socket> jugadorEspResp = new ConcurrentHashMap<>();
    private static final Map<Socket, Integer> personajesSecretos = new ConcurrentHashMap<>();
    private static final Map<Socket, Boolean> estaEnPartida = new ConcurrentHashMap<>();

    public static Partida partida;


    public static void main(String[] args) {
        try (ServerSocket server = new ServerSocket(PUERTO)) {
            System.out.println("Servidor Iniciado en el puerto: " + PUERTO);
            new Thread(() -> {
                while (true) {
                    try {
                        if (colaDeEspera.size() >= 2) {
                            Socket j1 = colaDeEspera.poll();
                            Socket j2 = colaDeEspera.poll();
                            if (j1 != null && j2 != null && j1.isConnected() && j2.isConnected() && !estaEnPartida.getOrDefault(j1, false) && !estaEnPartida.getOrDefault(j2, false)) {
                                estaEnPartida.put(j1, true);
                                estaEnPartida.put(j2, true);
                                oponentesEnPartida.put(j1, j2);
                                oponentesEnPartida.put(j2, j1);
                                iniciarNuevaPartida(j1, j2);
                            } else {
                                if (j1 != null && j1.isConnected() && !estaEnPartida.getOrDefault(j1, false)) colaDeEspera.add(j1);
                                if (j2 != null && j2.isConnected() && !estaEnPartida.getOrDefault(j2, false)) colaDeEspera.add(j2);
                            }
                        }
                        Thread.sleep(1000);
                    } catch (InterruptedException | IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            while (true) {
                Socket cliente = server.accept();
                System.out.println("Nuevo cliente conectado: " + cliente.getInetAddress());
                new Thread(() -> manejarCliente(cliente)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static synchronized void iniciarNuevaPartida(Socket jugador1, Socket jugador2) throws IOException {
        System.out.println("\nServidor: Iniciando nueva partida para " + clientesNickName.get(jugador1) + " y " + clientesNickName.get(jugador2));
        partida = new Partida();

        jugadorTurno.remove(jugador1);
        jugadorTurno.remove(jugador2);
        jugadorEspResp.remove(jugador1);
        jugadorEspResp.remove(jugador2);
        personajesSecretos.remove(jugador1);
        personajesSecretos.remove(jugador2);

        List<Personaje> personajes = PersonajeDB.generarTablero();
        if (personajes.isEmpty()) {
            System.err.println("Warning: La lista de personajes generada esta vacia");
            // Considera enviar un mensaje de error a los clientes y no iniciar la partida
            return;
        }

        String nickJugador1 = clientesNickName.get(jugador1);
        String nickJugador2 = clientesNickName.get(jugador2);

        partida.setJugador1(JugadorDB.conectarse(nickJugador1));
        partida.setJugador2(JugadorDB.conectarse(nickJugador2));

        ObjectOutputStream oos1 = clienteOutStream.get(jugador1);
        ObjectOutputStream oos2 = clienteOutStream.get(jugador2);

        oos1.reset();
        oos2.reset();

        PaqueteInicioPartida paqueteParaJ1 = new PaqueteInicioPartida(nickJugador2, new ArrayList<>(personajes));
        PaqueteInicioPartida paqueteParaJ2 = new PaqueteInicioPartida(nickJugador1, new ArrayList<>(personajes));

        oos1.writeObject(paqueteParaJ1);
        oos2.writeObject(paqueteParaJ2);
        System.out.println("Paquetes de inicio de partida enviados a los jugadores.");
    }

    private static void manejarCliente(Socket cliente) {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(cliente.getOutputStream());
            oos.flush();
            clienteOutStream.put(cliente, oos);
            ObjectInputStream ois = new ObjectInputStream(cliente.getInputStream());
            String nickJugador = (String) ois.readObject();
            if (nickJugador == null || nickJugador.trim().isEmpty()) {
                return;
            }
            clientesNickName.put(cliente, nickJugador);
            System.out.println("Nickname recibido: " + nickJugador);
            oos.writeObject("Servidor: Bienvenido, " + nickJugador + " Buscando partida...");
            estaEnPartida.put(cliente, false);
            while (cliente.isConnected()) {
                if (!estaEnPartida.getOrDefault(cliente, false) && !colaDeEspera.contains(cliente)) {
                    colaDeEspera.add(cliente);
                    System.out.println(nickJugador + " ha entrado en la cola de espera.");
                }
                while (!estaEnPartida.getOrDefault(cliente, false)) {
                    if (!cliente.isConnected()) return;
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                    }
                }
                Socket oponente = oponentesEnPartida.get(cliente);
                if (oponente == null || !oponente.isConnected()) {
                    System.out.println(clientesNickName.get(cliente) + ": Oponente no encontrado o desconectado. Volviendo a la cola.");
                    estaEnPartida.put(cliente, false);
                    oponentesEnPartida.remove(cliente);
                    continue;
                }
                System.out.println(nickJugador + " fue emparejado con " + clientesNickName.get(oponente));
                Object mensajeObj;
                while (cliente.isConnected() && oponente.isConnected() && estaEnPartida.getOrDefault(cliente, false)) {
                    try {
                        mensajeObj = ois.readObject();
                    } catch (IOException e) {
                        System.out.println("Cliente " + clientesNickName.get(cliente) + " cerró el stream de entrada. Desconectando.");
                        break;
                    }
                    if (!(mensajeObj instanceof String)) continue;
                    String mensaje = (String) mensajeObj;
                    System.out.println("Mensaje de " + nickJugador + ": " + mensaje);
                    if (mensaje.equals("JUGAR_OTRA_VEZ")) {
                        oponentesEnPartida.remove(oponente);
                        estaEnPartida.put(cliente, false);
                        estaEnPartida.put(oponente, false);
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
                        break;
                    }
                    if (mensaje.startsWith("PREGUNTA:")) {
                        if (cliente.equals(jugadorTurno.get(cliente))) {
                            String pregunta = mensaje.substring("PREGUNTA:".length()).trim();
                            clienteOutStream.get(oponente).writeObject("PREGUNTA:" + clientesNickName.get(cliente) + ":" + pregunta);
                            jugadorEspResp.put(cliente, oponente);
                        } else {
                            oos.writeObject("ERROR: No es tu turno para preguntar");
                        }
                    } else if (mensaje.startsWith("RESPUESTA:")) {
                        if (!cliente.equals(jugadorTurno.get(cliente)) && jugadorEspResp.containsKey(oponente) && jugadorEspResp.get(oponente).equals(cliente)) {
                            String respuesta = mensaje.substring("RESPUESTA:".length()).trim();
                            Socket jugadorPregunton = oponente;
                            clienteOutStream.get(jugadorPregunton).writeObject("RESPUESTA:" + clientesNickName.get(cliente) + ":" + respuesta);
                            jugadorEspResp.remove(oponente);
                            Thread.sleep(5000);
                            cambiarTurno(jugadorPregunton, cliente);
                        } else {
                            oos.writeObject("ERROR: No puedes responder en este momento");
                        }
                    } else if (mensaje.startsWith("ADIVINAR:")) {
                        String mensajeAdivinar = mensaje.substring("ADIVINAR:".length()).trim();
                        String[] partes = mensajeAdivinar.split(":");
                        int idAdivinado = Integer.parseInt(partes[0]);
                        long segundosRecibidos = Long.parseLong(partes[1]);
                        int idPersonajeSecretoOP = personajesSecretos.getOrDefault(oponente, -1);
                        String ganadorNick;
                        int idPersonajeGanadorSecreto;
                        if (idAdivinado == idPersonajeSecretoOP) {
                            ganadorNick = nickJugador;
                            idPersonajeGanadorSecreto = idPersonajeSecretoOP;
                            oos.writeObject("RESULTADO:GANASTE:" + ganadorNick);
                            clienteOutStream.get(oponente).writeObject("RESULTADO:PERDISTE:" + ganadorNick);
                        } else {
                            ganadorNick = clientesNickName.get(oponente);
                            idPersonajeGanadorSecreto = personajesSecretos.getOrDefault(oponente, -1);
                            oos.writeObject("RESULTADO:PERDISTE:" + ganadorNick);
                            clienteOutStream.get(oponente).writeObject("RESULTADO:GANASTE:" + ganadorNick);
                        }

                        partida.setWinner(ganadorNick);
                        partida.setPersonajeWinner(PersonajeDB.getPersonaje(idPersonajeGanadorSecreto, false, false, false));
                        partida.setTiempo(Duration.ofSeconds(segundosRecibidos));
                        partida.setFecha(LocalDate.now());
                        System.out.println("\nEl id del personaje ganador es: " + idPersonajeGanadorSecreto + "\n");
                        System.out.println(partida.toString() + "\n");
                        System.out.println("Partida a enviar: " + partida);
                        PartidaDB.insertarPartida(partida);

                        try {
                            clienteOutStream.get(cliente).writeObject(partida);
                            clienteOutStream.get(oponente).writeObject(partida);
                            System.out.println("Objeto Partida oficial enviado a los clientes.");
                        } catch (IOException e) {
                            System.err.println("Error al enviar el objeto Partida a los clientes.");
                            e.printStackTrace();
                        }
                        try {
                            clienteOutStream.get(cliente).writeObject("PERSONAJE_GANADOR_FINAL:" + idPersonajeGanadorSecreto);
                            clienteOutStream.get(oponente).writeObject("PERSONAJE_GANADOR_FINAL:" + idPersonajeGanadorSecreto);
                        } catch (Exception e) {
                            System.err.println("Error al enviar el ID del personaje ganador final: " + e.getMessage());
                        }
                        System.out.println("Partida terminada. " + nickJugador + " y " + clientesNickName.get(oponente) + " son libres.");
                        estaEnPartida.put(cliente, false);
                        estaEnPartida.put(oponente, false);

                        break;
                    } else if (mensaje.startsWith("PERSONAJE_ELEGIDO:")) {
                        synchronized (personajesSecretos) {
                            int idPersonajeElegido = Integer.parseInt(mensaje.substring("PERSONAJE_ELEGIDO:".length()).trim());
                            personajesSecretos.put(cliente, idPersonajeElegido);
                            if (personajesSecretos.containsKey(cliente) && personajesSecretos.containsKey(oponente)) {
                                System.out.println("Servidor: Ambos jugadores listos. Asignando turno inicial.");
                                asignarTurnoInicial(cliente, oponente);
                                oos.writeObject("INICIAR_CRONOMETRO");
                                clienteOutStream.get(oponente).writeObject("INICIAR_CRONOMETRO");
                            }
                        }
                    } else if (mensaje.equals("TURNO_TERMINADO")) {
                        if (cliente.equals(jugadorTurno.get(cliente))) {
                            cambiarTurno(cliente, oponente);
                        } else {
                            oos.writeObject("ERROR: No puedes terminar el turno si no es tuyo");
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Conexión perdida o error con cliente: " + e.getMessage());
            e.printStackTrace();
        } finally {
            String nick = clientesNickName.getOrDefault(cliente, "un cliente desconocido");
            System.out.println("Limpiando datos del jugador desconectado: " + nick);
            colaDeEspera.remove(cliente);
            Socket oponente = oponentesEnPartida.remove(cliente);
            if (oponente != null) {
                oponentesEnPartida.remove(oponente);
                estaEnPartida.put(cliente, false);
                estaEnPartida.put(oponente, false);
                jugadorTurno.remove(cliente);
                jugadorTurno.remove(oponente);
                jugadorEspResp.remove(cliente);
                jugadorEspResp.remove(oponente);
                personajesSecretos.remove(cliente);
                personajesSecretos.remove(oponente);
                if (clienteOutStream.containsKey(oponente)) {
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
                estaEnPartida.put(cliente, false);
            }
            clienteOutStream.remove(cliente);
            clientesNickName.remove(cliente);
            try {
                if (cliente != null && !cliente.isClosed()) cliente.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private static void asignarTurnoInicial(Socket j1, Socket j2) {
        try {
            Socket primerTurno = new Random().nextBoolean() ? j1 : j2;
            String nickTurno = clientesNickName.get(primerTurno);
            jugadorTurno.put(j1, primerTurno);
            jugadorTurno.put(j2, primerTurno);
            System.out.println("Turno asignado a: " + nickTurno);
            clienteOutStream.get(j1).writeObject("TU_TURNO:" + nickTurno);
            clienteOutStream.get(j2).writeObject("TU_TURNO:" + nickTurno);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void cambiarTurno(Socket jugadorActual, Socket oponente) {
        try {
            jugadorTurno.put(jugadorActual, oponente);
            jugadorTurno.put(oponente, oponente);
            String nickTurno = clientesNickName.get(oponente);
            System.out.println("El turno cambió a: " + nickTurno);
            clienteOutStream.get(jugadorActual).writeObject("TU_TURNO:" + nickTurno);
            clienteOutStream.get(oponente).writeObject("TU_TURNO:" + nickTurno);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}