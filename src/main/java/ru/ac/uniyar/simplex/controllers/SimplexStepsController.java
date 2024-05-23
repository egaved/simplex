package ru.ac.uniyar.simplex.controllers;

import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import ru.ac.uniyar.simplex.domain.Condition;
import ru.ac.uniyar.simplex.domain.SimplexTable;

import java.util.Arrays;

public class SimplexStepsController {

    private Stage stage;
    private Condition condition;

    @FXML
    private GridPane table;

    public void setProperties(Stage stage, Condition condition) {
        this.stage = stage;
        this.condition = condition;
    }

    public void onNextButtonClick() {
        SimplexTable st = new SimplexTable(condition);
        System.out.println(Arrays.deepToString(st.getElements()));
    }

    public void onPrevButtonClick() {

    }


    public void systemOutput(Condition condition) {
        System.out.println("varNum: "  + condition.getVariablesNum());
        System.out.println("restNum: " + condition.getRestrictionsNum() );
        System.out.println("min?: " + condition.getMinimize() );
        System.out.println("dec?: " + condition.getDecimals() );
        System.out.println("target: " + Arrays.toString(condition.getTargetFuncCoefficients()));
        System.out.println("restrict: " + Arrays.deepToString(condition.getRestrictionsCoefficients()));
        System.out.println("basis: " + condition.getBasis());
        System.out.println("artBas?: " + condition.getArtificialBasis());
    }
}
