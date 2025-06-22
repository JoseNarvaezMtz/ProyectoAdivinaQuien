package Classes;

import DataBaseClasses.JugadorDB;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDate;

public class Partida implements Comparable<Partida>, Serializable {
    private LocalDate fecha;
    private Duration tiempo;
    private String jugador1;
    private String jugador2;
    private String winner;
    private Personaje personajeWinner;

    public Partida(){}

    public Partida(String fecha, long time, int idj1, int idj2, int idW, int idP) {
        this.fecha = LocalDate.parse(fecha);
        this.tiempo = Duration.ofSeconds(time);
        this.jugador1 = JugadorDB.getJugador(idj1);
        this.jugador2 = JugadorDB.getJugador(idj2);
        this.winner = idW == idP ? this.jugador1 : this.jugador2;
    }

    @Override
    public int compareTo(Partida o) { return (int)( this.tiempo.toSeconds() - o.getTiempo().toSeconds() ); }

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

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public void setTiempo(Duration tiempo) {
        this.tiempo = tiempo;
    }

    public void setJugador1(String jugador1) {
        this.jugador1 = jugador1;
    }

    public void setJugador2(String jugador2) {
        this.jugador2 = jugador2;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public void setPersonajeWinner(Personaje personajeWinner) {
        this.personajeWinner = personajeWinner;
    }
}