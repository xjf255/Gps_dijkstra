package org.example.gps.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.example.gps.Model.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class ViewController {
    Graph graph = new Graph();
    @FXML
    private TextField fileNameTextField;

    @FXML
    protected void onLoadFile(ActionEvent event) {
        // Get the stage from the event source
        Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select a text file");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Text Files", "*.csv"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );

        File selectedFile = fileChooser.showOpenDialog(primaryStage);
        if (selectedFile != null) {
            graph.getInfoCSVNodo(selectedFile);
            //graph.generateAboutFile();
        }
    }
}