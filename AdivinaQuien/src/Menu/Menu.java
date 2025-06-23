package Menu;

// COMANDO PARA COMPILAR El PROGRAMA (SANTOS): --module-path "C:\javafx-sdk-24.0.1\lib" --add-modules javafx.controls,javafx.fxml --add-modules javafx.controls,javafx.fxml,javafx.media --add-exports javafx.base/com.sun.javafx=ALL-UNNAMED
// COMANDO PARA COMPILAR El PROGRAMA (JULIAN): --module-path "C:\Users\julia\Downloads\openjfx-24.0.1_windows-x64_bin-sdk\javafx-sdk-24.0.1\lib" --add-modules javafx.controls,javafx.fxml --add-modules javafx.controls,javafx.fxml,javafx.media --add-exports javafx.base/com.sun.javafx=ALL-UNNAMED
// COMANDO PARA COMPILAR El PROGRAMA (JOSÉ LUIS):
// COMANDO PARA COMPILAR El PROGRAMA (HARIM): --module-path "C:\Users\erick\openjfx-24.0.1_windows-x64_bin-sdk\javafx-sdk-24.0.1\lib" --add-modules javafx.controls,javafx.fxml --add-modules javafx.controls,javafx.fxml,javafx.media --add-exports javafx.base/com.sun.javafx=ALL-UNNAMED

import Sockets.Cliente;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCombination;
import javafx.scene.text.Font;
import javafx.stage.Stage;

// Clase principal que se compila

public class Menu extends Application {
    // Atributo para el cliente
    public static Cliente cliente;

    // Atributo para controlar si la aplicación esta en pantalla completa o ventana
    public static boolean fullScreen = true;

    // Atributo para almacenar el Nickname del jugador
    public static String nickName;

    // El main lo único que hace es llamar al metodo launch
    public static void main(String[] args) {
        launch(args);
    }

    // Metodo que se ejecuta al llamar al metodo launch, es decir, al correr la aplicación
    @Override
    public void start(Stage stage) throws Exception {
        try {
            // Cargar las fuentes utilizadas en el programa
            Font.loadFont(getClass().getResourceAsStream("/Fonts/PermanentMarker-Regular.ttf"),25);
            Font.loadFont(getClass().getResourceAsStream("/Fonts/CherryBombOne-Regular.ttf"),25);
            Font.loadFont(getClass().getResourceAsStream("/Fonts/FingerPaint-Regular.ttf"),25);

            // Crea la Stage inicial, y le asigna la escena del archivo FXML del menú principal
            Parent root = FXMLLoader.load(getClass().getResource("Menu.fxml"));
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/Menu/MenuStyles.css").toExternalForm());
            stage.setScene(scene);

            stage.setFullScreen(true); // Inicia la aplicación en pantalla completa
            stage.setFullScreenExitHint(""); // Esta línea omite el mensaje que se muetra al hacer la pantalla completa
            stage.setFullScreenExitKeyCombination(KeyCombination.valueOf("f11")); // Se asigna la tecla f11 para poner la aplicación en modo ventana

            // Cargar el ícono de la aplicación y el nombre de la ventana
            Image icon = new Image(getClass().getResourceAsStream("/Menu/Assets/tololitoIcono.jpg"));
            stage.getIcons().add(icon);
            stage.setTitle("Adivina el tolol");

            // Mostramos la aplicación
            stage.show();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}