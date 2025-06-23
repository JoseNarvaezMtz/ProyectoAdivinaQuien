package DataBaseClasses;

// Clase de la cual derivan todas las demas clases de base de datos

/*
    Esta clase no tiene algo en especial
    Solamente contiene el url para que se maneje en las clases de base de datos
 */

public abstract class dataBase {
    protected static final String url="jdbc:sqlite:DataBase.db";
}