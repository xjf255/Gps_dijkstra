package org.example.gps;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import org.example.gps.Model.*;
import java.io.IOException;
import java.util.HashMap;

public class MainApplication extends Application {
    // En org.example.gps.MainApplication.java
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 850, 700); // Ajusta el tamaño inicial

        String cssPath = "/styles/main.css"; // O el nombre que le hayas dado
        String css = getClass().getResource(cssPath).toExternalForm();
        if (css != null) {
            scene.getStylesheets().add(css);
        } else {
            System.err.println("No se pudo cargar el archivo CSS: " + cssPath);
        }

        stage.setTitle("GPS Dijkstra Visualizer");
        stage.setScene(scene);
        stage.show();
    }
}