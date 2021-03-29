// This will actually implement the GUi

import javafx.application.Application;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class PongGui extends Application {

    @Override
    public void start(Stage primaryStage){
        Pong gui = new Pong();

        StackPane rootPane = new StackPane();
        rootPane.getChildren().add(gui);

        Scene scene = new Scene(rootPane, 800, 900);
        //scene.setCursor(Cursor.NONE);
        primaryStage.setTitle("Pong");
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    public static void main(String[] args){
        Application.launch(args);
    }



}
