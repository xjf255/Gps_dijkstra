module org.example.gps {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;

    opens org.example.gps to javafx.fxml;
    exports org.example.gps;
    exports org.example.gps.Controller;
    opens org.example.gps.Controller to javafx.fxml;
}