module ru.ac.uniyar.simplex {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires commons.lang3;
    requires java.desktop;
    requires com.fasterxml.jackson.databind;

    opens ru.ac.uniyar.simplex to javafx.fxml;
    exports ru.ac.uniyar.simplex.windows;
    exports ru.ac.uniyar.simplex.controllers;
    exports ru.ac.uniyar.simplex;
    exports ru.ac.uniyar.simplex.domain;
    exports ru.ac.uniyar.simplex.calculations;
    opens ru.ac.uniyar.simplex.controllers to javafx.fxml;
    opens ru.ac.uniyar.simplex.windows to javafx.fxml;
}