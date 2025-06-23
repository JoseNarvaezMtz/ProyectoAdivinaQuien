package DataBaseClasses;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

/*
    Esta clase sirve para manejar las preguntas de la base de datos
    No tiene muchas cosas en especial
 */

public class PreguntasDB extends dataBase{

    /*
        Metodo obtenerPregunta

        Obtiene una pregunta mediante su id, al final no la utilizamos jajajaja
     */

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

    /*
        Metodo obtenerPreguntas

        Obtiene toda la lista de preguntas de la base de datos
     */

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