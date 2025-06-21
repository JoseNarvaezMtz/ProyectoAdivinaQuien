package DataBaseClasses;

import Classes.Personaje;
import javafx.scene.image.Image;
import javafx.scene.text.Text;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;

public class PersonajeDB extends dataBase {
    public static ArrayList<Personaje> generarTablero() {
        ArrayList<Integer> personajes = new ArrayList<>();
        int cont=0;

        while (cont < 24) {
            int id = (int)(Math.random() * 24);
            if (!personajes.contains(id)) {
                personajes.add(id);
                cont++;
            }
        }

        ArrayList<Personaje> lista = new ArrayList<>();

        String sql = "SELECT * FROM Personajes WHERE id = ?";

        try (Connection con = DriverManager.getConnection(url);
             PreparedStatement stmt = con.prepareStatement(sql);) {

            for (int i=0; i<personajes.size(); i++) {
                int id = personajes.get(i);
                stmt.setInt(1, id);
                Personaje personaje = new Personaje();

                try(ResultSet rs = stmt.executeQuery();){
                    if (rs.next()) {
                        personaje.setIdTablero(i);
                        personaje.setId(id);
                        personaje.setNombre(rs.getString("nombre"));
                        personaje.setImagen(rs.getBytes("imagen"));
                        personaje.setTachado(false);
                        // Aqui cambiamos lo que le pasamos, para pasarle el string
                        personaje.setDescripcion(rs.getString("descripcion"));
                    }
                    lista.add(personaje);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return lista;
    }

    public static ArrayList<Personaje> getPersonajes() {
        ArrayList<Personaje> lista = new ArrayList<>();

        String sql = "SELECT * FROM Personajes";

        try (Connection con = DriverManager.getConnection(url);
             PreparedStatement stmt = con.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Personaje personaje = new Personaje();
                personaje.setId(rs.getInt("id"));
                personaje.setNombre(rs.getString("nombre"));
                personaje.setImagen(rs.getBytes("imagen"));
                // Aqui cambiamos lo que le pasamos, para pasar el string
                personaje.setDescripcion(rs.getString("descripcion"));
                lista.add(personaje);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return lista;
    }

    public static Personaje getPersonaje(int id, boolean nombre, boolean imagen, boolean descripcion) {
        String sql = "SELECT * FROM Personajes WHERE id = ?";
        Personaje personaje = new Personaje();

        try (Connection con = DriverManager.getConnection(url);
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()) {

                if (nombre)
                    personaje.setNombre(rs.getString("nombre"));
                if (imagen) {
                    personaje.setImagen(rs.getBytes("imagen"));
                }
                if (descripcion) {
                    // Aqui cambiamos lo que le pasamos, para pasarel el string
                    personaje.setDescripcion(rs.getString("descripcion"));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return personaje;
    }

    public static void insertarPersonajes() {
        String[] nombres = {
                "Mia la Panda", "Zaki el Zorro", "Navi el Pinguino", "Rolo el Mono",
                "Tambo el Elefante", "Liam el Leon", "Sabi el Buho", "Kiko el Koala",
                "Tari el Tigre", "Dana la Panda", "Yelo el Pinguino"
        };

        String[] historias = {
                "Mia la Panda es una amante del invierno. Desde pequeña soñaba con explorar las montanas nevadas en busca de los copos de nieve mas raros del mundo. Con su gorro naranja de lana y su bufanda de colores brillantes, Mia recorre valles helados y glaciares misteriosos. Lleva un diario donde dibuja cada hallazgo y describe las temperaturas, los paisajes y los sonidos del viento entre los arboles congelados. Su curiosidad la ha llevado incluso a descubrir una cueva secreta donde los cristales de hielo cantan al amanecer.",
                "Zaki el Zorro es un explorador nato, conocido por su gran sentido de la orientacion y su camara siempre lista para capturar el instante perfecto. Viaja de selva en selva, de desierto en desierto, con su sombrero de paja y sus gafas gigantes que le permiten ver detalles que otros pasan por alto. Zaki colecciona imagenes unicas: huellas raras, atardeceres sobre dunas doradas y hasta criaturas que creian extintas. Su objetivo es crear un atlas visual de la naturaleza salvaje para inspirar a futuras generaciones de exploradores.",
                "Navi el Pinguino nacio en una pequeña colonia al sur del oceano polar. Desde temprana edad sintio fascinacion por el mar abierto y los barcos. Con su gorra de capitan y collares brillantes, Navi navega entre icebergs en un pequeño rompehielos modificado por el mismo. En cada viaje documenta aves marinas, focas y misterios del fondo del mar. Escribe relatos de tormentas que ha sobrevivido, de auroras boreales que lo dejaron sin aliento, y de encuentros con ballenas jorobadas que nadan junto a su embarcacion por horas.",
                "Rolo el Mono es la chispa del grupo, siempre alegre y con nuevas ideas para explorar la selva. Con su camisa llena de flores y su gorra azul, Rolo guia a los demas por senderos cubiertos de lianas y hojas gigantes. Conoce cada canto de ave, cada fruta comestible y cada insecto curioso del lugar. Tiene una mochila llena de cuadernos con mapas hechos a mano, dibujos y recetas con ingredientes tropicales. Su gran sueno es construir una casa del arbol gigante donde todos sus amigos puedan pasar la noche bajo las estrellas.",
                "Tambo el Elefante es grande en tamano y en corazon. Es el cartografo oficial de la expedicion, siempre cargando sus maletines llenos de pergaminos, lapices y tintas de colores. Tambo estudia el terreno con paciencia, sube colinas, observa el sol y calcula distancias con solo mirar el horizonte. Cada mapa que dibuja es una obra de arte detallada, con simbolos propios, rutas secretas y anotaciones de todo lo que encuentran en el camino. Su mayor deseo es crear un atlas mundial de aventuras animales.",
                "Liam el Leon es el guardian del grupo. Con su melena siempre bien peinada y su sombrero de viaje, acompaña a sus amigos por selvas, rios y desiertos, siempre atento a cualquier peligro. Aunque es fuerte y valiente, tambien tiene un lado tierno y protector. Guarda historias de sus antepasados en un cuaderno que le regalo su abuelo y siempre esta dispuesto a compartir una leyenda antes de dormir. Su mochila verde contiene herramientas de supervivencia, una cuerda multiusos y una pequena tienda de campana.",
                "Sabi el Buho es el sabio del grupo. Con su sombrero azul puntiagudo y sus gafas redondas, lleva un libro magico lleno de conocimientos antiguos. No solo sabe leer mapas y estrellas, sino que puede descifrar simbolos antiguos y contar historias olvidadas. Sabi habla varios idiomas de animales y escribe poesia sobre las aventuras que viven juntos. En su mochila lleva plumas de aves raras, pergaminos antiguos y una lupa de aumento con la que analiza cada descubrimiento que hacen.",
                "Kiko el Koala es un observador silencioso pero muy valiente. Camina entre arboles y hojas altas, siempre buscando un nuevo sendero que lo lleve a descubrir maravillas naturales. Tiene una increible capacidad para encontrar refugios seguros y lugares con vistas panoramicas. Kiko lleva una libreta donde anota cada flor, hongo y especie que encuentra. Le encanta subir a lo mas alto de los arboles para ver el atardecer y pensar en las rutas para el dia siguiente. Su mochila esta llena de bocadillos, agua y una linterna.",
                "Tari el Tigre es el estratega del grupo. Siempre con un mapa en la mano y una bufanda roja, disena rutas que combinan emocion, belleza y descubrimiento. Es curioso por naturaleza y nunca se rinde cuando algo no sale como espera. Tiene un instinto infalible para encontrar ruinas antiguas, cavernas ocultas y rios subterraneos. A cada lugar que van, Tari hace una marca en su mapa personal que suena con colgar algun dia en una gran sala de aventuras.",
                "Dana la Panda es una observadora detallista. Con su mochila azul y mono rojo, se encarga de documentar cada momento del viaje con palabras y dibujos. Dana escribe cuentos sobre los lugares que visitan, las personas que conocen y los secretos que descubren. Es muy creativa y suele narrar las aventuras como si fueran capitulos de un gran libro. Su cuaderno esta lleno de ilustraciones a color, hojas secas pegadas, fotografias y anecdotas que hacen reir a todos alrededor de la fogata.",
                "Yelo el Pinguino es el fotografo oficial del equipo. Con su camara colgando siempre del cuello y su look invernal, captura cada detalle que los demas pasan por alto: huellas, reflejos en el hielo, miradas de asombro. Yelo ha ganado premios en revistas de viajes y sueña con tener su propia exposicion de fotos en un museo. Cada noche revisa sus imagenes, selecciona las mejores y las clasifica por tema, lugar y emocion. Siempre dice que una buena foto es aquella que cuenta una historia sin palabras."
        };

        String sql = "INSERT INTO Personajes (imagen, nombre, descripcion) VALUES (?, ?, ?)";

        try (Connection con = DriverManager.getConnection(url);
             PreparedStatement stmt = con.prepareStatement(sql)) {

             for(int i = 24; i < 35; i++) {
                 String ruta = "C:/Users/josen/Downloads/personajesPT2/Personaje" + i + ".jpg";
                 byte[] bytes = Files.readAllBytes(Paths.get(ruta));
                 stmt.setBytes(1, bytes);
                 stmt.setString(2, nombres[i-24]);
                 stmt.setString(3, historias[i-24]);
                 stmt.executeUpdate();
             }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
