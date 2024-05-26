package ru.ac.uniyar.simplex.controllers;

import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import ru.ac.uniyar.simplex.domain.Condition;
import ru.ac.uniyar.simplex.domain.SimplexTable;
import ru.ac.uniyar.simplex.secondary.Coordinate;

import java.util.Arrays;
import java.util.Objects;

public class SimplexStepsController {

    private Stage stage;
    private Condition condition;
    private SimplexTable simplex;
    private Rectangle lastSelectedRect = null;
    private Coordinate selectedPivot = null;

    @FXML
    private GridPane table;

    @FXML
    private Label pivotLabel;

    public void setProperties(Stage stage, Condition condition) {
        this.stage = stage;
        this.condition = condition;
        this.simplex = new SimplexTable(condition);
    }



    public void init() {
        int cols = simplex.getFreeVariables().size() + 2;
        int rows = simplex.getBasicVariables().size() + 2;

        for (int i = 0; i < cols; i++) {
            String columnHeader;
            if (i == 0 || i == cols - 1) columnHeader = " ";
            else columnHeader = "x" + simplex.getFreeVariables().get(i - 1);
            Label columnLabel = new Label(columnHeader);
            columnLabel.setFont(new Font(16));
            table.add(columnLabel, i, 0);
            GridPane.setHalignment(columnLabel, HPos.CENTER);
        }

        for (int i = 1; i <= rows - 1; i++) {
            table.addRow(i);
            for (int j = 0; j < cols; j++) {
                if (j == 0) {
                    String tempStr;
                    if (i != rows - 1) {
                        tempStr = "x" + simplex.getBasicVariables().get(i - 1);
                    } else {
                        tempStr = "";
                    }

                    Label rowLabel = new Label(tempStr);
                    rowLabel.setFont(new Font(16));
                    table.add(rowLabel, 0, i);
                } else {
                    String value;
                    if (simplex.getElements()[i - 1][j - 1].getDenominator() == 1)
                        value = String.valueOf(simplex.getElements()[i - 1][j - 1].getNumerator());
                    else
                        value = simplex.getElements()[i - 1][j - 1].toString();

                    Coordinate coordinate = new Coordinate((i - 1), (j - 1));
                    Rectangle rect = createRectangle(coordinate, value);

                    Label label = new Label(value);
                    label.setPrefSize(50, 30);
                    label.setAlignment(Pos.CENTER);

                    StackPane stackPane = new StackPane(); // stackpane для наложения текста на прямоугольник
                    stackPane.getChildren().addAll(rect, label);

                    for (Coordinate p : simplex.getPivots()) {
                        if (
                                Objects.equals(coordinate.getRowIndex(), p.getRowIndex())
                                        &&
                                        Objects.equals(coordinate.getColIndex(), p.getColIndex())
                        ) {
                            stackPane.setOnMouseClicked(mouseEvent -> {
                                if (lastSelectedRect != rect) {
                                    rect.setFill(Color.LIGHTSKYBLUE);
                                    // если последний нажатый прямоугольник не равен null, возвращаем ему прозрачный цвет
                                    if (lastSelectedRect != null) {
                                        lastSelectedRect.setFill(Color.LIGHTCYAN);
                                    }
                                    lastSelectedRect = rect; // обновляем ссылку на последний нажатый прямоугольник
                                }
                                selectedPivot = new Coordinate(coordinate.getRowIndex(), coordinate.getColIndex());
                                pivotLabel.setText("Опорный элемент: " + value);
                                pivotLabel.setFont(new Font(20));
                            });
                        }
                    }

                    table.add(stackPane, j, i);
                }
            }
        }
    }



    private Rectangle createRectangle(Coordinate coordinate, String value) {
        Rectangle rect = new Rectangle(50, 30);
        rect.setStroke(Color.BLACK);
        rect.setStrokeWidth(2);
        rect.setFill(Color.TRANSPARENT);

        for (Coordinate p : simplex.getPivots()) {
            if (
                    Objects.equals(coordinate.getRowIndex(), p.getRowIndex())
                            &&
                            Objects.equals(coordinate.getColIndex(), p.getColIndex())
            ) {
                rect.setFill(Color.LIGHTCYAN);
            }
        }

        return rect;
    }


    public void onNextButtonClick() {

    }

    public void onPrevButtonClick() {

    }


    public void systemOutput(Condition condition) {
        System.out.println("varNum: " + condition.getVariablesNum());
        System.out.println("restNum: " + condition.getRestrictionsNum());
        System.out.println("min?: " + condition.getMinimize());
        System.out.println("dec?: " + condition.getDecimals());
        System.out.println("target: " + Arrays.toString(condition.getTargetFuncCoefficients()));
        System.out.println("restrict: " + Arrays.deepToString(condition.getRestrictionsCoefficients()));
        System.out.println("basis: " + condition.getBasis());
        System.out.println("artBas?: " + condition.getArtificialBasis());
    }
}
