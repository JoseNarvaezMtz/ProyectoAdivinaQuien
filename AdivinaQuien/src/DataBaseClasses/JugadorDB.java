package DataBaseClasses;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

// Clase que manejara la base de datos de los jugadores, para todos los aspectos necesarios
/*
    Todos los metodos se conectan especificamente a la tabla de jugadores
    Si alguna otra clase utiliza a la tabla de jugadores, usará un metodo de aqui
 */

public class JugadorDB extends dataBase{

    /*
        Metodo de verificarJugador

        Este metodo solamente realiza una verificacion de que el nombre exista en la base de datos
        Forma parte de la realizacion del metodo de conectarse
     */

    public static boolean verificarJugador(String name) {
        String sql = "SELECT * FROM Jugadores WHERE nombre = ?";
        try (Connection con = DriverManager.getConnection(url); PreparedStatement stmt = con.prepareStatement (sql)) {

            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /*
        Metodo de conectarse

        Solamente verifica que el nombre este en la base de datos, si no esta lo registra
        Solo funciona como un pase para la base de datos, fuera de lo de la base de datos
        no realiza nada
     */

    public static String conectarse(String name) {
        if (!verificarJugador(name)) {
            registrarJugador(name);
        }
        return name;
    }

    /*
        Metodo de registrarJugador

        Este metodo agrega al usuario a la base de datos
        Forma parte del metodo de conectarse
    */

    public static void registrarJugador(String name) {
        String sql = "INSERT INTO Jugadores (nombre) VALUES (?)";

        try (Connection con = DriverManager.getConnection(url);
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, name);
            stmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
        Metodo getJugador

        Metodo el cual obtiene al jugador mediante el id del jugador
     */

    public static String getJugador(int id) {
        String sql = "SELECT * FROM Jugadores WHERE id = ?";

        try (Connection con = DriverManager.getConnection(url); PreparedStatement stmt = con.prepareStatement (sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getString("nombre");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /*
        Metodo el cual consigue el id del jugador mediante su nombre
        Usado para guardar en partidas los usuarios
     */

    public static int obtenerID(String nombre) {
        String sql = "SELECT * FROM Jugadores WHERE nombre = ?";

        try (Connection con = DriverManager.getConnection(url); PreparedStatement stmt = con.prepareStatement (sql)) {

            stmt.setString(1, nombre);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    /*
        Metodo de obtenerIds

        Obtiene una lista de ids en base a un fragmento del nombre, usado para la tabla de busqueda por nombre
     */

    public static ArrayList<Integer> obtenerIds(String name) {

        ArrayList<Integer> lista = new ArrayList<>();

        String sql = "SELECT * FROM Jugadores WHERE nombre LIKE ?";

        try (Connection con = DriverManager.getConnection(url); PreparedStatement stmt = con.prepareStatement (sql)) {

            stmt.setString(1, "%" + name + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                lista.add(rs.getInt("id"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return lista;
    }
}
