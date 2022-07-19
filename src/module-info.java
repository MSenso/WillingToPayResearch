module MarkovModelApplication {
    requires javafx.fxml;
    requires javafx.controls;
    requires javafx.graphics;
    requires java.desktop;
    exports org.openjfx.run;
    exports org.openjfx.controller;
    exports org.openjfx.model;
    opens org.openjfx.controller to javafx.fxml;
    opens org.openjfx.run to javafx.fxml;
    opens org.openjfx.model to javafx.fxml;
}

