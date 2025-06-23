package TerminarPartida;

import Menu.Menu;
import Menu.MenuController;
import Sockets.Cliente;
import Tablero.TableroController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class TerminarPartidaController extends MenuController implements Initializable {

    @FXML Pane rootPane;

    @FXML ImageView fondoImage;
    @FXML GridPane gridPane;
    @FXML GridPane gridPane2;

    @FXML Label tituloLabel;

    @FXML Button buttonRegresarMenu;
    @FXML Button buttonVolverAJugar;

    //Musica
    public MediaPlayer musica;
    Media musicWin = new Media(getClass().getResource("/TerminarPartida/Assets/sunny.mp3").toString());
    Media musicLost = new Media(getClass().getResource("/TerminarPartida/Assets/rain.mp3").toString());
    AudioClip sonidoClick = new AudioClip(getClass().getResource("/TerminarPartida/Assets/confirmTab.mp3").toString());

    // Referencia para el cliente y asi poder cerrarlo
    private Cliente cliente;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        if (TableroController.musica != null) {
            TableroController.musica.stop();
        }

        if (MenuController.musica != null){
            MenuController.musica.stop();
        }


        javafx.application.Platform.runLater(() -> {
            Stage stage = (Stage) rootPane.getScene().getWindow();
            stage.setFullScreen(Menu.fullScreen);
        });


        // Ajuste general de tamaños de los contenedores
        fondoImage.fitWidthProperty().bind(rootPane.widthProperty());
        fondoImage.fitHeightProperty().bind(rootPane.heightProperty());

        gridPane2.prefWidthProperty().bind(rootPane.widthProperty().divide(1.5));
        gridPane2.prefHeightProperty().bind(rootPane.heightProperty().divide(2));

        tituloLabel.prefWidthProperty().bind(rootPane.widthProperty());
        tituloLabel.prefHeightProperty().bind(rootPane.heightProperty().divide(2));
        tituloLabel.setFont(new javafx.scene.text.Font(80));
        tituloLabel.setWrapText(true);
        tituloLabel.setMaxWidth(Double.MAX_VALUE);

        // Ajustar el tamaño de los botones con grid pane
        buttonVolverAJugar.prefWidthProperty().bind(rootPane.widthProperty().divide(3));
        buttonVolverAJugar.prefHeightProperty().bind(rootPane.heightProperty().divide(7));

        buttonRegresarMenu.prefWidthProperty().bind(rootPane.widthProperty().divide(3));
        buttonRegresarMenu.prefHeightProperty().bind(rootPane.heightProperty().divide(7));

        // Ajuste de gridPane
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(100);
        col1.setHgrow(Priority.ALWAYS);

        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(100);
        col2.setHgrow(Priority.ALWAYS);

        gridPane2.getColumnConstraints().addAll(col1, col2);

        GridPane.setHgrow(buttonVolverAJugar, Priority.ALWAYS);
        GridPane.setVgrow(buttonVolverAJugar, Priority.ALWAYS);

        GridPane.setHgrow(buttonRegresarMenu, Priority.ALWAYS);
        GridPane.setVgrow(buttonRegresarMenu, Priority.ALWAYS);

        Image imagenVolverAJugar = new Image(getClass().getResourceAsStream("/TerminarPartida/Assets/jugar.png"));
        ImageView imageView = new ImageView(imagenVolverAJugar);
        imageView.setFitWidth(60);
        imageView.setFitHeight(60);
        buttonVolverAJugar.setGraphic(imageView);
        buttonVolverAJugar.setMaxWidth(Double.MAX_VALUE);

        Image imagenRegresarAMenu = new Image(getClass().getResourceAsStream("/TerminarPartida/Assets/tabla.png"));
        ImageView imageView2 = new ImageView(imagenRegresarAMenu);
        imageView2.setFitWidth(60);
        imageView2.setFitHeight(60);
        buttonRegresarMenu.setGraphic(imageView2);
        buttonRegresarMenu.setMaxWidth(Double.MAX_VALUE);

    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    //Toma al padre para mandar a llamar al metodo del padre que manda al usuario a partidas registradas
    @Override
    public void partidasRegistradas(ActionEvent e) throws IOException {
        // Hacemos sonido al hacer click
        sonidoClick();

        // Si el usuario tiene la musica habilitada
        if(MenuController.desicionUsuario == true ){
            MenuController.musica.play();
        }

        // Detenemos la musica
        musica.stop();

        // Cerramos el cliente antes de ir al menu
        if (cliente != null) {
            System.out.println("TerminarPartida: Cerrando cliente antes de ir al menu.");
            Menu.cliente.desconexion();

            // Limpiamos el cliente
            Menu.cliente = null;
            this.cliente = null;
        }
        super.partidasRegistradas(e); // Llamamos al metodo del padre para ir a partidas registradas
    }

    // Metodo para volver al menú principal y cerrar el cliente
    @FXML // Asegúrate de que el FXML del botón "Volver a Jugar" apunte a este método
    public void volverAlMenuPrincipal(ActionEvent e) {
        try {
            sonidoClick(); // Reproduce tu sonido de click

            // Cierra la conexión del cliente si existe
            if (cliente != null) {
                System.out.println("TerminarPartida: Desconectando cliente para regresar al menú principal.");
                cliente.enviarMensaje("JUGAR_OTRA_VEZ", ""); // Informa al servidor que este cliente quiere cerrar o reencolar (aunque luego lo cerraremos)
                cliente.desconexion(); // Cierra el socket
                Menu.cliente = null; // Elimina la referencia global
                this.cliente = null; // Elimina la referencia local
            } else {
                System.out.println("TerminarPartida: Cliente ya nulo o cerrado al intentar volver al menú.");
            }

            //Detiene la música de la pantalla de fin de partida
            if (musica != null) {
                musica.stop();
            }

            // Establece la bandera volver a jugar en MenuController a true
            // Para que al llegar sepa mostrar el panel del nickname
            MenuController.volverAJugar = true;

            // Carga la escena del menú principal (donde se asigna el nickname)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Menu/Menu.fxml"));
            Parent menuRoot = loader.load();

            Scene nuevaScene = new Scene(menuRoot);
            // Aplica los estilos CSS del menú
            nuevaScene.getStylesheets().add(getClass().getResource("/Menu/MenuStyles.css").toExternalForm());

            // Obtiene el Stage actual y cambia la escena
            Stage stage = (Stage)((Node)e.getSource()).getScene().getWindow();
            stage.setScene(nuevaScene);
            stage.show();

        } catch (IOException ex) {
            System.err.println("¡ERROR AL VOLVER AL MENÚ PRINCIPAL DESDE TERMINAR PARTIDA!");
            ex.printStackTrace();
        }
    }

    public void redireccionPantalla(boolean estado){
        //Dependiendo de si ganó o perdió modifica los botones
        if (estado) {

            // Botones y nodo de la pantalla
            buttonVolverAJugar.getStyleClass().add("buttonPartidaFinWin");
            buttonRegresarMenu.getStyleClass().add("buttonPartidaFinWin");
            tituloLabel.getStyleClass().add("tituloPartidaFinWin");

            // Musica
            musica = new MediaPlayer(musicWin);
            musica.setCycleCount(MediaPlayer.INDEFINITE);
            musica.setVolume(0.2);
            musica.play();

            // Configuracion para el inicio de la pantalla
            fondoImage.setImage(new javafx.scene.image.Image(getClass().getResourceAsStream("/TerminarPartida/Assets/winner.png")));
            tituloLabel.setText("Felicidades, Ganaste!");

        }
        else{

            // Botones y nodo de la pantalla
            tituloLabel.getStyleClass().add("tituloPartidaFinLose");
            buttonVolverAJugar.getStyleClass().add("buttonPartidaFinLose");
            buttonRegresarMenu.getStyleClass().add("buttonPartidaFinLose");

            // Musica
            musica = new MediaPlayer(musicLost);
            musica.setCycleCount(MediaPlayer.INDEFINITE);
            musica.setVolume(0.2);
            musica.play();

            // Configuracion para el inicio de la pantalla
            fondoImage.setImage(new javafx.scene.image.Image(getClass().getResourceAsStream("/TerminarPartida/Assets/loser.png")));
            tituloLabel.setText("Perdiste, Lo siento!");
        }


    }
    public void sonidoClick(){
        sonidoClick.setVolume(0.2);
        sonidoClick.play();
    }

    public void onPersonajeGanador(){

    }
}
