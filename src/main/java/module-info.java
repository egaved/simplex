module ru.ac.uniyar.simplex {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;

    opens ru.ac.uniyar.simplex to javafx.fxml;
    exports ru.ac.uniyar.simplex;
}