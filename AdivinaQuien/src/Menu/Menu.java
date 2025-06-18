package Menu;

// COMANDO PA CORRER ESTA MAMADA (SANTOS): --module-path "C:\javafx-sdk-24.0.1\lib" --add-modules javafx.controls,javafx.fxml
// COMANDO PA CORRER ESTA MAMADA (JULIAN): --module-path "C:\Users\julia\Downloads\openjfx-24.0.1_windows-x64_bin-sdk\javafx-sdk-24.0.1\lib" --add-modules javafx.controls,javafx.fxml
// COMANDO PA CORRER ESTA MAMADA (JOSÉ LUIS):
// COMANDO PA CORRER ESTA MAMADA (HARIM): --module-path "C:\Users\erick\openjfx-24.0.1_windows-x64_bin-sdk\javafx-sdk-24.0.1\lib" --add-modules javafx.controls,javafx.fxml --add-modules javafx.controls,javafx.fxml,javafx.media --add-exports javafx.base/com.sun.javafx=ALL-UNNAMED

/*
    TODO
     (SANTOS) Intentar implementar el controlador principal para evitar varias stages
     Adaptar tamaño de la mayoría de íconos de botones a la resolución del dispositivo
     Adaptar el tamaño de las fuentes a la resolución del dispositivo

    TODO
     Comentar el código y refactorizar pa que no este t0do qlero
     Maybe agregar texturas a algunas cosas
*/

import Sockets.Cliente;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCombination;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class Menu extends Application {
    // Atributo para el cliente
    public static Cliente cliente;

    public static void main(String[] args) {
        launch(args);
    }
    public static boolean fullScreen = true;
    public static String nickName;

    @Override
    public void start(Stage stage) throws Exception {

        try {
            Font.loadFont(getClass().getResourceAsStream("/Fonts/PermanentMarker-Regular.ttf"),25);
            Font.loadFont(getClass().getResourceAsStream("/Fonts/CherryBombOne-Regular.ttf"),25);
            Font.loadFont(getClass().getResourceAsStream("/Fonts/FingerPaint-Regular.ttf"),25);
            Parent root = FXMLLoader.load(getClass().getResource("Menu.fxml"));
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/Menu/MenuStyles.css").toExternalForm());
            stage.setScene(scene);
            stage.setFullScreen(true);
            stage.setFullScreenExitHint("");
            stage.setFullScreenExitKeyCombination(KeyCombination.valueOf("f11"));
            Image icon = new Image(getClass().getResourceAsStream("/Menu/Assets/tololitoIcono.jpg"));
            stage.getIcons().add(icon);
            stage.setTitle("Adivina el tolol");
            stage.show();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}