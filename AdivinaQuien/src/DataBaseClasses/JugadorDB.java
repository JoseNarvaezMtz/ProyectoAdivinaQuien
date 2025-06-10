package DataBaseClasses;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class JugadorDB extends dataBase{

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

    public static String conectarse(String name) {
        if (verificarJugador(name))
            registrarJugador(name);
        return name;
    }

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
}
