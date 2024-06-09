package ru.ac.uniyar.simplex.windows;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import ru.ac.uniyar.simplex.controllers.SimplexStepsController;
import ru.ac.uniyar.simplex.domain.Condition;
import ru.ac.uniyar.simplex.domain.SimplexTable;

import java.io.IOException;

public class SimplexStepsWindow {

    public void display(Condition condition) {
        try {
            Stage stage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ru/ac/uniyar/simplex/simplex-steps.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            if (condition.getArtificialBasis()) {
                stage.setTitle("Симплекс-метод. Шаги.");
                stage.setScene(scene);
                SimplexStepsController controller = fxmlLoader.getController();
                controller.setProperties(stage, condition);
                controller.init(new SimplexTable(condition));
            } else {
                stage.setTitle("Искусствуенный базис. Шаги.");
                stage.setScene(scene);
                SimplexStepsController controller = fxmlLoader.getController();
                controller.setProperties(stage, condition);
                controller.init(new SimplexTable(condition));
            }
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
