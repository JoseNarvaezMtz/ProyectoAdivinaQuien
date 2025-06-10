package Classes;

import DataBaseClasses.JugadorDB;

import java.time.Duration;
import java.time.LocalDate;

public class Partida {
    private LocalDate fecha;
    private Duration tiempo;
    private String jugador1;
    private String jugador2;
    private String winner;
    private Personaje personajeWinner;

    public Partida(String fecha, long time, int idj1, int idj2, int idW, int idP) {
        this.fecha = LocalDate.parse(fecha);
        this.tiempo = Duration.ofSeconds(time);
        this.jugador1 = JugadorDB.getJugador(idj1);
        this.jugador2 = JugadorDB.getJugador(idj2);
        this.winner = idW == idP ? this.jugador1 : this.jugador2;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public Duration getTiempo() {
        return tiempo;
    }

    public String getJugador1() {
        return jugador1;
    }

    public String getJugador2() {
        return jugador2;
    }

    public String getWinner() {
        return winner;
    }

    public Personaje getPersonajeWinner() {
        return personajeWinner;
    }
}
