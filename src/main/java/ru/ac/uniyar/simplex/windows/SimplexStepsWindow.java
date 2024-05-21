package ru.ac.uniyar.simplex.windows;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import ru.ac.uniyar.simplex.controllers.SimplexStepsController;
import ru.ac.uniyar.simplex.domain.Condition;

import java.io.IOException;

public class SimplexStepsWindow {

    public void display(Condition condition) {
        try {
            Stage stage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ru/ac/uniyar/simplex/simplex-steps.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            stage.setTitle("Симплекс-метод. Шаги.");
            stage.setScene(scene);
            SimplexStepsController controller = fxmlLoader.getController();
            controller.setProperties(stage, condition);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
