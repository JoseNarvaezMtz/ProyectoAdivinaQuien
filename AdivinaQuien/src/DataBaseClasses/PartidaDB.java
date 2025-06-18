package DataBaseClasses;

import Classes.Partida;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.util.ArrayList;

public class PartidaDB extends dataBase{

    public void insertarPartida(Partida partida) {
        String fecha = partida.getFecha().toString();
        int tiempo = partida.getTiempo().toSecondsPart();
        int idj1 = JugadorDB.obtenerID(partida.getJugador1());
        int idj2 = JugadorDB.obtenerID(partida.getJugador2());
        int idg = JugadorDB.obtenerID(partida.getWinner());
        int idp = partida.getPersonajeWinner().getId();

        try (Connection con = DriverManager.getConnection(url)) {
            String insert = "INSERT INTO Partidas(fecha, tiempo, id_jugador1, " +
                    "id_jugador2, id_ganador, id_personaje_ganador) " +
                    "VALUES(?, ?, ?, ?, ?, ?)";

            try (PreparedStatement statement = con.prepareStatement(insert)) {
                statement.setString(1, fecha);
                statement.setInt(2, tiempo);
                statement.setInt(3, idj1);
                statement.setInt(4, idj2);
                statement.setInt(5, idg);
                statement.setInt(6, idp);
                statement.executeUpdate();
                System.out.println("Partida ingresada");
            }
        }
        catch (SQLException e) {
            System.err.println("Error en la conexion: " + e.getMessage());
        }
    }

    public static ObservableList<ObservableList<String>> getHistorialPartidas() {
        ObservableList<ObservableList<String>> lista = FXCollections.observableArrayList();

        try (Connection con = DriverManager.getConnection(url)) {
            String select = "SELECT * FROM Partidas";

            try (Statement statement = con.createStatement();
                 ResultSet rs = statement.executeQuery(select)) {
                String fecha;
                String tiempo;
                String jugador1;
                String jugador2;
                String ganador;
                String personaje;
                while (rs.next()) {
                    fecha=rs.getString("fecha");
                    tiempo= Integer.toString(rs.getInt("tiempo"));
                    jugador1=JugadorDB.getJugador(rs.getInt("id_jugador1"));
                    jugador2=JugadorDB.getJugador(rs.getInt("id_jugador2"));
                    ganador=JugadorDB.getJugador(rs.getInt("id_ganador"));
                    System.out.println(jugador1 + " " + jugador2 + " " + ganador + " " + rs.getInt("id_ganador"));
                    personaje=PersonajeDB.getPersonaje(rs.getInt("id_personaje_ganador"),true, false,false).getNombre();
                    ObservableList<String> aux= FXCollections.observableArrayList(
                            jugador1, jugador2, ganador, personaje,
                            fecha, tiempo
                    );
                    lista.add(aux);
                }
                System.out.println("Lista de partidas preparada");
            }
        } catch (SQLException e) {
            System.err.println("Error en la conexion: " + e.getMessage());
        }

        return lista;
    }

    public static ObservableList<ObservableList<String>> getPorNombre(String name) {
        if (name.equals(""))
            return getHistorialPartidas();

        ObservableList<ObservableList<String>> lista = FXCollections.observableArrayList();

        ArrayList<Integer> ids = JugadorDB.obtenerIds(name);
        ArrayList<Integer> idP = new ArrayList<>();
        String sql = "SELECT * FROM Partidas WHERE id_jugador1 = ? OR id_jugador2 = ?"; // NO.

        try (Connection con = DriverManager.getConnection(url); PreparedStatement stmt = con.prepareStatement (sql)) {
                for (int id : ids) {
                    stmt.setInt(1, id);
                    stmt.setInt(2, id);
                    String fecha;
                    String tiempo;
                    String jugador1;
                    String jugador2;
                    String ganador;
                    String personaje;
                    ResultSet rs = stmt.executeQuery();
                    while (rs.next()) {
                        int pID = rs.getInt("id");
                        if (!idP.contains((pID))) {
                            fecha = rs.getString("fecha");
                            tiempo = Integer.toString(rs.getInt("tiempo"));
                            jugador1 = JugadorDB.getJugador(rs.getInt("id_jugador1"));
                            jugador2 = JugadorDB.getJugador(rs.getInt("id_jugador2"));
                            ganador = JugadorDB.getJugador(rs.getInt("id_ganador"));
                            System.out.println(jugador1 + " " + jugador2 + " " + ganador + " " + rs.getInt("id_ganador"));
                            personaje = PersonajeDB.getPersonaje(rs.getInt("id_personaje_ganador"), true, false, false).getNombre();
                            ObservableList<String> aux = FXCollections.observableArrayList(
                                    jugador1, jugador2, ganador, personaje,
                                    fecha, tiempo
                            );
                            lista.add(aux);
                            idP.add(pID);
                        }
                    }
                    System.out.println("Lista de partidas preparada");
            }
        } catch (SQLException e) {
            System.err.println("Error en la conexion: " + e.getMessage());
        }
        return lista;
    }

    public static ObservableList<ObservableList<String>> getHistorialOrdenado() {

        ObservableList<ObservableList<String>> lista = FXCollections.observableArrayList();
        String sql = "SELECT * FROM Partidas ORDER BY tiempo ASC";
        try (Connection con = DriverManager.getConnection(url); PreparedStatement stmt = con.prepareStatement (sql)) {
            String fecha;
            String tiempo;
            String jugador1;
            String jugador2;
            String ganador;
            String personaje;
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                fecha = rs.getString("fecha");
                tiempo = Integer.toString(rs.getInt("tiempo"));
                jugador1 = JugadorDB.getJugador(rs.getInt("id_jugador1"));
                jugador2 = JugadorDB.getJugador(rs.getInt("id_jugador2"));
                ganador = rs.getInt("id_ganador") == rs.getInt("id_jugador1") ? jugador1 : jugador2;
                System.out.println(jugador1 + " " + jugador2 + " " + ganador + " " + rs.getInt("id_ganador"));
                personaje = PersonajeDB.getPersonaje(rs.getInt("id_personaje_ganador"), true, false, false).getNombre();
                ObservableList<String> aux = FXCollections.observableArrayList(
                        jugador1, jugador2, ganador, personaje,
                        fecha, tiempo
                );
                lista.add(aux);
            }
            System.out.println("Lista de partidas preparada");
        } catch (SQLException e) {
            System.err.println("Error en la conexion: " + e.getMessage());
        }
        return lista;
    }
}
