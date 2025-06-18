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
                "Berto el hipopotamo",
                "Luna la panda",
                "Fenix el zorro",
                "Raji el tigre",
                "Polo el pinguino",
                "Nilo el elefante",
                "Simon el leon",
                "Gala la jirafa",
                "Mono Tico",
                "Koa el coyote",
                "Aran el tigre",
                "Doki el panda",
                "Zafiro el zorro",
                "Bubu el buho",
                "Frosty el pinguino",
                "Simba el leon",
                "Jali la jirafa",
                "Mono Tico",
                "Kari la canguro",
                "Eli el elefante",
                "Orik el buho",
                "Hipoc el hipopotamo",
                "Leo el leon",
                "Jira la jirafa"
        };

        String[] historias = {
                "Berto el hipopotamo era un jardinero alegre que cuidaba un enorme invernadero lleno de plantas exóticas. Cada mañana se ponia su gorra morada y sus gafas anaranjadas, tomaba su regadera y recorria el jardin cantando. Su mochila verde estaba siempre llena de semillas, herramientas y libros de botanica. A pesar de su gran tamaño, Berto era cuidadoso con cada flor, hablaba con ellas y decia que entendian su cariño. Una vez al mes, organizaba una feria de plantas donde enseñaba a los niños a sembrar. Todos lo querian porque hacia brotar la alegria como si fuera primavera eterna.",
                "Luna la panda aventurera llevaba su mochila verde brillante a todas partes. Con su sombrero verde y gafas grandes, caminaba por los senderos del bosque en busca de nuevas rutas. Cada vez que encontraba una flor rara o una piedra curiosa, la anotaba en su libreta. Su pañoleta roja era un recuerdo de su primera expedicion, donde ayudo a una tortuga perdida a regresar a casa. Le encantaba contar historias junto al fuego, y su sueno era escribir un libro de viajes para otros pandas curiosos. Cada paso suyo era un poema de descubrimiento.",
                "Fenix el zorro era el detective mas famoso del bosque. Siempre con su sombrero gris y sus lentes azules, caminaba sigilosamente con una lupa en la mano. En su chaleco guardaba pistas y un mapa arrugado lleno de marcas misteriosas. Resolvio el caso del arbol robado y descubrio quien robaba las zanahorias del huerto. A pesar de su seriedad, le gustaba reir y tomar chocolate caliente despues de resolver un caso. Su oficina era una cabaña pequena repleta de libros y fotos. Todos confiaban en Fenix, porque nunca fallaba.",
                "Raji el tigre era un pequeno explorador con un gran sueno: recorrer todo el mundo. Llevaba un avion de juguete en su mochila y un mapa que estudiaba cada noche. Sus lentes verdes y gorra roja lo hacian ver como un piloto listo para despegar. Le fascinaba aprender sobre animales y culturas diferentes. Una vez, organizo un viaje imaginario en su patio trasero con sus amigos, donde cada rincon era un nuevo pais. Raji creia que los mapas eran tesoros y que cada paso era una aventura esperando ser contada.",
                "Polo el pinguino amaba las tardes frias, el chocolate caliente y los libros de historias fantasticas. Con su gorro naranja y bufanda de cuadros, se sentaba junto al iglu a leer en voz alta para sus amigos. Siempre cargaba una taza caliente y un libro bajo el ala. Su biblioteca era pequena pero llena de magia. Cada historia que leia la vivia con emocion, y a veces escribia sus propias aventuras. Decia que en cada pagina habia una puerta a otro mundo. Todos lo admiraban por su pasion por las palabras y su sonrisa calida.",
                "Nilo el elefante era un pintor talentoso que transformaba paisajes en arcoiris sobre lienzo. Usaba un sombrero amarillo y lentes redondos, y siempre cargaba su paleta de colores. En su chaleco guardaba pinceles y bocetos. Su eleccion favorita era pintar al amanecer, cuando el sol tocaba las hojas. Una vez, pinto un mural gigante en la escuela del bosque que decia 'La belleza esta en los ojos curiosos'. Nilo inspiraba a todos a ver el arte en lo cotidiano. Su trompa dejaba huellas de creatividad donde iba.",
                "Simon el leon era el entrenador de futbol del bosque. Siempre llevaba su silbato, un balon y una gorra roja. Sus lentes oscuros lo hacian ver serio, pero era el mas divertido del equipo. Ensenaba a jugar con respeto y pasion. Cada entrenamiento era una fiesta de goles y risas. Gano muchos torneos pero siempre decia que lo importante era jugar con el corazon. Su rugido de animo levantaba el espiritu de todos. Simon era el rey del campo, no por su melena, sino por su amor al juego.",
                "Gala la jirafa era una observadora nata. Con su sombrero amarillo con flor y gafas violetas, recorria los prados con sus binoculares y una libreta azul. Apuntaba cada descubrimiento: el vuelo de una mariposa, el color de una piedra. Su mochila estaba llena de flores recolectadas con cuidado. Decia que cada dia traia un milagro para quien supiera mirar. Su cuarto era un museo de cosas pequenas con historias grandes. Gala enseñaba a los demas a ver el mundo con ojos nuevos.",
                "Tico el mono era el guia de los senderos secretos. Con su sombrero de ala ancha, gafas turquesas y una banana siempre en mano, recorria el bosque con su mapa gastado. Cada sendero tenia una historia, cada arbol un nombre. Sus aventuras estaban llenas de risas y descubrimientos. Ayudo a muchos a no perderse, y les contaba chistes para animarlos. Tico decia que el bosque era un gran libro abierto, solo habia que saber leerlo. Su energia era contagiosa y su espiritu, libre como el viento.",
                "Koa el coyote era un amante de la velocidad y el estilo. Con su gorro rojo, gafas verdes y patineta azul, cruzaba las colinas como un rayo. Siempre llevaba una bebida fria y musica en sus oidos. Practicaba trucos en las rampas de los troncos caidos. Su chaqueta celeste tenia parches de sus viajes y su mochila estaba llena de recuerdos. Koa decia que cada curva era una oportunidad para volar. Los demas lo seguian por su espiritu rebelde y su sonrisa intrépida.",
                "Aran el tigre era un explorador valiente con una lanza hecha a mano. Usaba gorra azul y gafas grandes para observar desde lo alto de los arboles. Su mochila llevaba una piedra especial que segun el, le daba suerte. Siempre buscaba ruinas antiguas y marcas en las rocas. Le encantaba resolver acertijos de mapas viejos. Sus botas llenas de lodo eran prueba de sus hazanas. Aran creia que el mundo escondia secretos para quienes se atrevian a buscar.",
                "Doki el panda viajero llevaba su gorra roja con orgullo. Con lentes grandes, camara colgando y un mapa en la mano, recorria lugares exoticos. Su bebida favorita era jugo de naranja con pajilla. Sacaba fotos de todo, desde flores raras hasta nubes curiosas. Llevaba un diario donde dibujaba sus recorridos. Ayudo una vez a una abeja perdida a encontrar su colmena. Doki inspiraba a otros pandas a descubrir el mundo y capturar momentos unicos.",
                "Zian el zorro era un investigador curioso. Sus gafas rojas y su lupa azul eran sus herramientas inseparables. Con su gorro verde y su cuaderno, recorria los senderos buscando respuestas. Investigaba hojas raras, huellas misteriosas y sonidos en la noche. Su mochila estaba llena de frascos, lapices y mapas. Una vez descubrio una especie nueva de flor y la nombro en honor a su abuela. Zian decia que la ciencia era magia con datos. Todos querian aprender de el.",
                "Bubu el buho era el guardian de la sabiduria nocturna. Con su gorra azul de estrellas y su linterna, guiaba a los demas en la oscuridad. Usaba lentes grandes y tenia una bolsa llena de mapas antiguos. En su mochila llevaba pergaminos enrollados. Le gustaba leer bajo la luz de la luna y contar historias miticas. Decia que cada estrella tenia un cuento y que la noche era el momento perfecto para aprender. Bubu era silencioso pero sabio, y todos lo respetaban.",
                "Frosty el pinguino disfrutaba del invierno como nadie. Con su gorro naranja y gafas azules, caminaba entre la nieve con su paleta helada. Llevaba una mochila roja llena de regalos y una taza de chocolate caliente. Su alegria era contagiosa. Organizaba juegos en el hielo y decoraba los arboles congelados. Cada vez que alguien estaba triste, le ofrecía un dulce y una sonrisa. Frosty creia que la felicidad estaba en compartir los pequenos momentos.",
                "Simba el leon era un guardian de la laguna. Con su chaleco azul decorado con un patito amarillo, silbato colgado al cuello y lentes verdes, vigilaba que todos jugaran seguros. Tenia una banda roja con estrellas y su gorra favorita. Le gustaba organizar carreras de flotadores y enseñar a nadar. Una vez salvo a un erizo que se habia caido al agua. Simba era fuerte y amable, y todos los animales confiaban en el.",
                "Jali la jirafa adoraba la naturaleza. Con su sombrero lleno de flores, gafas de corazon y red para mariposas, exploraba los campos. Su mochila estaba llena de plantas, semillas y dibujos. Cada dia descubria una nueva flor o insecto. Tenia una libreta donde escribia poemas sobre lo que veia. Jali decia que cada flor era una amiga esperando ser descubierta. Su ternura y curiosidad la hacian unica.",
                "Mono Tico era el lider de la expedicion al volcan. Con su gorra verde y gafas rojas, llevaba una banana en una mano y un mapa en la otra. Tenia un cinturon lleno de herramientas y una bebida para recargar energia. Guiaba al grupo por caminos empinados y contaba historias sobre el fuego antiguo. Encontraron cristales brillantes gracias a sus ideas. Tico era valiente y siempre encontraba la mejor ruta.",
                "Kari el canguro era una guia tierna con un peluche en su mochila. Llevaba sombrero de explorador y gafas azules. Usaba una brujula para orientarse y un mapa plastificado. Cuidaba a los pequenos del grupo y les contaba cuentos mientras caminaban. Su osito de peluche era su companero desde bebe. Kari decia que los mejores viajes eran los que se hacian con el corazon abierto. Todos la querian como una hermana mayor.",
                "Eli el elefante organizaba la mejor fiesta de cumpleanos del bosque. Con su sombrero de colores, lentes morados y pastel en la mano, repartia globos y abrazos. Su traje azul con moño era famoso. Decoraba todo con estrellas y organizaba juegos para todos. Una vez, hizo un pastel tan grande que todos comieron dos veces. Eli decia que cada cumpleanos era una oportunidad para agradecer. Su alegria iluminaba el dia de todos.",
                "Orik el buho era un mago del conocimiento. Con su sombrero puntiagudo y tunica azul, llevaba un baston brillante y un libro antiguo. Usaba lentes redondos y tenia plumas doradas. Enseñaba hechizos de luz y mapas del cielo estrellado. Iluminaba cuevas con su baston y leia en lenguas olvidadas. Orik inspiraba respeto y asombro. Decia que la magia verdadera era compartir lo que uno sabia.",
                "Hipoc el hipopotamo era un chef encantador. Con su gorro blanco, gafas azules y delantal con manchas de colores, cocinaba frutas para todos. Su cuchara de madera era su varita magica. Hacia jugos naturales, ensaladas y postres saludables. Ensenaba a los ninos a amar la comida sana. Su cocina olia a naranja y fresa. Hipoc decia que cocinar era otra forma de dar amor. Todos esperaban sus meriendas con emocion.",
                "Leo el leon era un aventurero clasico. Con sombrero verde, lentes celestes y mochila cafe, recorria ruinas antiguas y selvas espesas. Usaba un cuaderno para anotar descubrimientos. Encontraba huellas, huesos y amuletos perdidos. Ayudo una vez a una familia de armadillos a encontrar su hogar. Leo era curioso y valiente. Decia que cada paso en la selva era una pagina nueva en su libro de vida.",
                "Jira la jirafa era una fotografa talentosa. Con su gorro morado, gafas rojas y camara colgando, capturaba la belleza de la sabana. Su mochila estaba llena de rollos y baterias. Esperaba horas por la toma perfecta. Una vez fotografio una tormenta desde lejos, y la foto gano un premio. Jira decia que una imagen podia contar mil emociones. Sus fotos decoraban la galeria del bosque."
        };

        String sql = "INSERT INTO Personajes (imagen, nombre, descripcion) VALUES (?, ?, ?)";

        try (Connection con = DriverManager.getConnection(url);
             PreparedStatement stmt = con.prepareStatement(sql)) {

             for(int i = 0; i < 24; i++) {
                 String ruta = "C:/Users/josen/OneDrive/Documentos/4to Semestre/Progra III/ProyectoAQ/AdivinaQuienDataBase/AdivinaQuien Sockets/AdivinaQuien Sockets/AdivinaQuien/AdivinaQuien/src/Tablero/Assets/Personaje" + i + ".jpg";
                 byte[] bytes = Files.readAllBytes(Paths.get(ruta));
                 stmt.setBytes(1, bytes);
                 stmt.setString(2, nombres[i]);
                 stmt.setString(3, historias[i]);
                 stmt.executeUpdate();
             }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
