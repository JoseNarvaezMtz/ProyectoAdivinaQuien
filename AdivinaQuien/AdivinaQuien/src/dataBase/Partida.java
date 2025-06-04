package dataBase;

import java.time.Duration;
import java.time.LocalDate;


public class Partida {
    private int id;
    private LocalDate fecha;
    private Duration tiempo;
    private int idj1, idj2, idg, idpj1, idpj2;

    public Partida(int id, String fecha, int duracion, int idj1, int idj2, int idg, int idpj1, int idpj2) {
        this.id = id;
        this.fecha = LocalDate.parse(fecha);
        this.tiempo = Duration.ofSeconds(duracion);
        this.idj1=idj1;
        this.idj2=idj2;
        this.idg=idg;
        this.idpj1=idpj1;
        this.idpj2=idpj2;
    }

    public Partida() {
        this.fecha=LocalDate.now();
        this.tiempo = Duration.ofSeconds((int)(Math.random()*100)+120);
        this.idj1 = 1;
        this.idj2 = 2;
        this.idg = (int)(Math.random()*1)+1;
        this.idpj1 = 4;
        this.idpj2 = 7;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public Duration getTiempo() {
        return tiempo;
    }

    public void setTiempo(Duration tiempo) {
        this.tiempo = tiempo;
    }

    public int getIdj1() {
        return idj1;
    }

    public void setIdj1(int idj1) {
        this.idj1 = idj1;
    }

    public int getIdj2() {
        return idj2;
    }

    public void setIdj2(int idj2) {
        this.idj2 = idj2;
    }

    public int getIdg() {
        return idg;
    }

    public void setIdg(int idg) {
        this.idg = idg;
    }

    public int getIdpj1() {
        return idpj1;
    }

    public void setIdpj1(int idpj1) {
        this.idpj1 = idpj1;
    }

    public int getIdpj2() {
        return idpj2;
    }

    public void setIdpj2(int idpj2) {
        this.idpj2 = idpj2;
    }

    @Override
    public String toString() {
        return "Partida: \n" +
                "- id:" + id + "\n" +
                "- fecha: " + fecha + "\n" +
                "- tiempo: " + tiempo.getSeconds() + " segundos\n" +
                "- idj1: " + idj1 + "\n" +
                "- idj2: " + idj2 + "\n" +
                "- idg: " + idg + "\n" +
                "- idpj1: " + idpj1 + "\n" +
                "- idpj2: " + idpj2 + "\n";
    }
}
