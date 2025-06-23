package Classes;

import java.io.Serializable;
import java.util.List;

public class PaqueteInicioPartida implements Serializable {
    private static final long serialVersionUID = 1L; // Buena práctica para clases serializables

    private final String oponenteNick;
    private final List<Personaje> personajes;

    public PaqueteInicioPartida(String oponenteNick, List<Personaje> personajes) {
        this.oponenteNick = oponenteNick;
        this.personajes = personajes;
    }

    public String getOponenteNick() {
        return oponenteNick;
    }

    public List<Personaje> getPersonajes() {
        return personajes;
    }
}

