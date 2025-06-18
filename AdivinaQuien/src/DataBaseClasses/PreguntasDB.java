package DataBaseClasses;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class PreguntasDB extends dataBase{
    public static String obtenerPregunta(int id) {
        String sql = "SELECT * FROM Preguntas WHERE id = ?";

        try (Connection con = DriverManager.getConnection(url); PreparedStatement stmt = con.prepareStatement (sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getString("pregunta");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ArrayList<String> obtenerPreguntas () {
        String sql = "SELECT * FROM Preguntas";

        ArrayList<String> preguntas = new ArrayList<>();

        try (Connection con = DriverManager.getConnection(url); PreparedStatement stmt = con.prepareStatement (sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                preguntas.add(rs.getString("pregunta"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return preguntas;
    }
}