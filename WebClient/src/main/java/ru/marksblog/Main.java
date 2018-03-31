package ru.marksblog;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application{

    public static void main(String[] args){
        //client2=new Client();
       // Client client=new Client(client2.getAddress(),client2.getPort());
        //new Thread(client).start();
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("App.fxml"));
        primaryStage.setTitle("WebClient");
        primaryStage.setScene(new Scene(root, 600, 600));
        primaryStage.show();
        root.getStylesheets();

    }
}
