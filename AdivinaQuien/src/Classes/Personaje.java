package Classes;

import javafx.scene.image.Image;
import javafx.scene.text.Text;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.util.Arrays;

// Clase para instanciar personajes del juego

public class Personaje implements Serializable { // Implementamos la clase para serializar para mandarlo por sockets
    /* Incluimos su version esto es importante por que de esta manera
     manejamos de manera correcta a los distintos objetos serializables*/
    private static final long serialVersionUID = 1L;

    private int id;                 // Id del personaje
    private String nombre;          // Nombre del personaje
    private byte[] imagen;          // Imágen del personaje
    private int idTablero;          // Id en el tablero del personaje
    private boolean tachado;        // Booleano para saber si el personaje está tachado en el tablero
    private String descripcion;     // Descripción del personaje

    // Getters y Setters

    public void setId(int id) {
        this.id = id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setImagen(byte[] imagen) {
        this.imagen = imagen;
    }

    public void setIdTablero(int idTablero) {
        this.idTablero = idTablero;
    }

    public void setTachado(boolean tachado) {
        this.tachado = tachado;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    // Metodo para cuando se necesite en JavaFX
    public Image getImagenFX() {return new Image(new ByteArrayInputStream(imagen));}

    // Metodo para cuando se necesite en la serializacion
    public byte[] getImagen() {return imagen;}

    public int getIdTablero() {
        return idTablero;
    }

    public boolean isTachado() {
        return tachado;
    }

    // Metodo para cuando se use la descripcion en la serializacion
    public String getDescripcionString() {
        return descripcion;
    }

    // Metodo para cuando se quiera usar la descripcion en JavaFX
    public Text getDescripcionText() {
        return new Text(descripcion);
    }

    // Metodo ToString
    @Override
    public String toString() {
        return "Personaje{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", imagen=" + Arrays.toString(imagen) +
                ", idTablero=" + idTablero +
                ", tachado=" + tachado +
                ", descripcion='" + descripcion + '\'' +
                '}';
    }
}
