package dataBase;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.util.ArrayList;

public class ConexionDB {
    private static final String url="jdbc:sqlite:ProyectoAQ.db";

    public ConexionDB() {}

    public static void insertarPartida(Partida partida) {
        String fecha = partida.getFecha().toString();
        int tiempo = partida.getTiempo().toSecondsPart();
        int idj1 = partida.getIdj1();
        int idj2 = partida.getIdj2();
        int idg = partida.getIdg();
        int idpj1 = partida.getIdpj1();
        int idpj2 = partida.getIdpj2();

        try (Connection con = DriverManager.getConnection(url)) {
            String insert = "INSERT INTO Partidas(fecha, tiempo, id_jugador1, " +
                    "id_jugador2, id_ganador, id_personaje_jugador1, id_personaje_jugador2) " +
                    "VALUES(?, ?, ?, ?, ?, ?, ?)";

            try (PreparedStatement statement = con.prepareStatement(insert)) {
                statement.setString(1, fecha);
                statement.setInt(2, tiempo);
                statement.setInt(3, idj1);
                statement.setInt(4, idj2);
                statement.setInt(5, idg);
                statement.setInt(6, idpj1);
                statement.setInt(7, idpj2);
                statement.executeUpdate();
                System.out.println("Partida ingresada");
            }
        }
        catch (SQLException e) {
            System.err.println("Error en la conexion: " + e.getMessage());
        }
    }

    public static ArrayList<Partida> leerPartidas() {
        ArrayList<Partida> lista = new ArrayList<>();
        try (Connection con = DriverManager.getConnection(url)) {
            String select = "SELECT id, fecha, tiempo, id_jugador1, " +
                    "id_jugador2, id_ganador, id_personaje_jugador1, id_personaje_jugador2 " +
                    "FROM Partidas";

            try (Statement statement = con.createStatement();
                 ResultSet result = statement.executeQuery(select)) {
                int id;
                String fecha;
                int tiempo;
                int idj1;
                int idj2;
                int idg;
                int idpj1;
                int idpj2;
                while (result.next()) {
                    id=result.getInt("id");
                    fecha=result.getString("fecha");
                    tiempo=result.getInt("tiempo");
                    idj1=result.getInt("id_jugador1");
                    idj2=result.getInt("id_jugador2");
                    idg=result.getInt("id_ganador");
                    idpj1=result.getInt("id_personaje_jugador1");
                    idpj2=result.getInt("id_personaje_jugador2");
                    lista.add(new Partida(id, fecha, tiempo, idj1, idj2, idg, idpj1, idpj2));
                }
                System.out.println("Lista de partidas preparada");
            }
        } catch (SQLException e) {
            System.err.println("Error en la conexion: " + e.getMessage());
        }
        return lista;
    }

    public static ObservableList<ObservableList<String>> getHistorialPartidas() {
        ObservableList<ObservableList<String>> lista = FXCollections.observableArrayList();

        try (Connection con = DriverManager.getConnection(url)) {
            String select = "SELECT id, fecha, tiempo, id_jugador1, " +
                    "id_jugador2, id_ganador, id_personaje_jugador1, id_personaje_jugador2 " +
                    "FROM Partidas";

            try (Statement statement = con.createStatement();
                 ResultSet result = statement.executeQuery(select)) {
                int id;
                String fecha;
                int tiempo;
                int idj1;
                int idj2;
                int idg;
                int idpj1;
                int idpj2;
                while (result.next()) {
                    id=result.getInt("id");
                    fecha=result.getString("fecha");
                    tiempo=result.getInt("tiempo");
                    idj1=result.getInt("id_jugador1");
                    idj2=result.getInt("id_jugador2");
                    idg=result.getInt("id_ganador");
                    idpj1=result.getInt("id_personaje_jugador1");
                    idpj2=result.getInt("id_personaje_jugador2");
                    ObservableList<String> aux= FXCollections.observableArrayList(
                            Integer.toString(idj1), Integer.toString(idj2), fecha, Integer.toString(tiempo),
                            Integer.toString(idg), Integer.toString(idpj1)
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

}
