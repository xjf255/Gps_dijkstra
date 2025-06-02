package org.example.gps; // O el paquete donde tengas MainApplication

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("hello-view.fxml"));
        // El tamaño inicial que pones aquí (850, 700) será el tamaño si la ventana se restaura desde maximizado.
        Scene scene = new Scene(fxmlLoader.load(), 850, 700);

        String cssPath = "/styles/main.css"; // O el nombre de tu archivo CSS principal
        try {
            String css = getClass().getResource(cssPath).toExternalForm();
            scene.getStylesheets().add(css);
        } catch (NullPointerException e) {
            System.err.println("No se pudo cargar el archivo CSS: " + cssPath + ". Asegúrate de que la ruta sea correcta y el archivo exista en resources/styles.");
        }

        stage.setTitle("GPS Dijkstra Visualizer");
        stage.setScene(scene);

        // --- LÍNEA A AÑADIR/ASEGURAR QUE ESTÉ PRESENTE ---
        stage.setMaximized(true);
        // -------------------------------------------------

        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}