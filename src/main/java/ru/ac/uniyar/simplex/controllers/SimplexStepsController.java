package ru.ac.uniyar.simplex.controllers;

import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.apache.commons.lang3.math.Fraction;
import ru.ac.uniyar.simplex.domain.Condition;
import ru.ac.uniyar.simplex.domain.SimplexTable;
import ru.ac.uniyar.simplex.secondary.Coordinate;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class SimplexStepsController {

    private Stage stage;
    private LinkedList<SimplexTable> steps;
    private Condition condition;
    private SimplexTable simplex;
    private Rectangle lastSelectedRect = null;
    private Coordinate selectedPivot = null;
    private int currentStep;
    private boolean artificialBasis;

    @FXML
    private GridPane table;
    @FXML
    private Label pivotLabel;
    @FXML
    private Label basisLabel;
    @FXML
    private Button nextButton;
    @FXML
    private Button prevButton;
    @FXML
    private Label stepLabel;

    public void setProperties(Stage stage, Condition condition) {
        this.stage = stage;
        this.condition = condition;

        this.simplex = new SimplexTable();
        if (!condition.getMinimize()) {
            maxToMin(condition);
        }
        this.simplex = new SimplexTable(condition);
        this.steps = new LinkedList<>();
        steps.add(simplex);
        currentStep = 0;
        nextButton.setDisable(true);
        prevButton.setDisable(true);
    }

    public void maxToMin(Condition condition) {
        String[] oldTargetCoefs = condition.getTargetFuncCoefficients();
        String[] newTargetCoefs = new String[oldTargetCoefs.length];
        for (int i = 0; i < oldTargetCoefs.length; i++) {
            Fraction negative = Fraction.getFraction(oldTargetCoefs[i]).multiplyBy(Fraction.getReducedFraction(-1, 1));
            newTargetCoefs[i] = negative.toString();
        }
        condition.setTargetFuncCoefficients(newTargetCoefs);
    }

    public void init(SimplexTable simplexTable) {
        if (simplexTable.isArtBasis()) stepLabel.setText("Шаг (иск. базис): " + currentStep);
        else stepLabel.setText("Шаг (симплекс): " + currentStep);
        int cols = simplexTable.getFreeVariables().size() + 2;
        int rows = simplexTable.getBasicVariables().size() + 2;

        for (int i = 0; i < cols; i++) {
            String columnHeader;
            if (i == 0 || i == cols - 1) columnHeader = " ";
            else columnHeader = "x" + simplexTable.getFreeVariables().get(i - 1);
            Label columnLabel = new Label(columnHeader);
            columnLabel.setFont(new Font(16));
            table.add(columnLabel, i, 0);
            GridPane.setHalignment(columnLabel, HPos.CENTER);
        }
        AtomicBoolean f = new AtomicBoolean(false); //выбран ли опорный
        for (int i = 1; i <= rows - 1; i++) {
            table.addRow(i);
            for (int j = 0; j < cols; j++) {
                if (j == 0) {
                    String tempStr;
                    if (i != rows - 1) {
                        tempStr = "x" + simplexTable.getBasicVariables().get(i - 1);
                    } else {
                        tempStr = "";
                    }

                    Label rowLabel = new Label(tempStr);
                    rowLabel.setFont(new Font(16));
                    table.add(rowLabel, 0, i);
                } else {
                    String value;
                    if (simplexTable.getElements()[i - 1][j - 1].getDenominator() == 1)
                        value = String.valueOf(simplexTable.getElements()[i - 1][j - 1].getNumerator());
                    else
                        value = simplexTable.getElements()[i - 1][j - 1].toString();

                    Coordinate coordinate = new Coordinate((i - 1), (j - 1));
                    Rectangle rect = createRectangle(coordinate, simplexTable.getPivots());

                    Label label = new Label(value);
                    label.setPrefSize(50, 30);
                    label.setAlignment(Pos.CENTER);

                    StackPane stackPane = new StackPane(); // stackpane для наложения текста на прямоугольник
                    stackPane.getChildren().addAll(rect, label);

                    for (Coordinate p : simplexTable.getPivots()) {
                        if (
                                Objects.equals(coordinate.getRowIndex(), p.getRowIndex())
                                        &&
                                        Objects.equals(coordinate.getColIndex(), p.getColIndex())
                        ) {
                            stackPane.setOnMouseClicked(mouseEvent -> {
                                f.set(true);
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
                if (!f.get() && !simplexTable.getPivots().isEmpty()) {
                    selectedPivot = new Coordinate(simplexTable.getPivots().getFirst().getRowIndex(),
                            simplexTable.getPivots().getFirst().getColIndex());
                    pivotLabel.setText("Опорный элемент: "
                            + simplexTable.getElements()[selectedPivot.getRowIndex()][selectedPivot.getColIndex()]);
                    pivotLabel.setFont(new Font(20));
                }
            }

        }

        if (simplexTable.getPivots().isEmpty()) {
            // получаем ссылку на последний элемент таблицы
            StackPane sp = (StackPane) table.getChildren().getLast();

            // получаем ссылку на label в stakpane
            Label label = (Label) sp.getChildren().get(1);

            // получаем значение label
            String value = label.getText();
            Fraction answer = Fraction.getFraction(value);
            if (condition.getMinimize()) {
                answer = answer.multiplyBy(Fraction.getFraction(-1, 1));
            }
            if (answer.getDenominator() == 1) {
                pivotLabel.setText("Ответ: " + answer.getNumerator());
                String basis = createBasisString(simplexTable);
                basisLabel.setText("x* = " + basis);
            }
            else {
                pivotLabel.setText("Ответ: " + answer);
            }

            if(hasStepAfterAB()) {
                pivotLabel.setText("Базис найден.");
                basisLabel.setText("");
            }
            pivotLabel.setFont(new Font(20));
            basisLabel.setFont(new Font(20));
        }
        nextButton.setDisable(!hasNextStep(this.simplex) && !hasStepAfterAB());
        prevButton.setDisable(currentStep == 0);
        if (simplexTable.isUnbounded()) {
            pivotLabel.setText("Решение не ограничено (ребро уходит в бесконечность)");
            pivotLabel.setFont(new Font(20));
            nextButton.setDisable(true);
        } else if (simplexTable.hasNoFreeVars()) {
            pivotLabel.setText("Не осталось свободных переменных.");
            pivotLabel.setFont(new Font(20));
            nextButton.setDisable(true);
        }
    }

    private String createBasisString(SimplexTable simplexTable) {

        Fraction[] answer = new Fraction[simplexTable.getBasicVariables().size() + simplexTable.getFreeVariables().size()];
        String[] result = new String[answer.length];
        for (int i = 0; i < answer.length; i++) {
            answer[i] = Fraction.getFraction("0");
        }

        for (int i = 0; i < simplexTable.getBasicVariables().size(); i++) {
            int varNum = simplexTable.getBasicVariables().get(i);
            answer[varNum - 1] =  simplexTable.getElements()[i][simplexTable.getFreeVariables().size()];
        }

        for (int i = 0; i < result.length; i++) {
            if (answer[i].getDenominator() == 1)
                result[i] = String.valueOf(answer[i].getNumerator());
            else
                result[i] = String.valueOf(answer[i]);
        }

        return Arrays.toString(result);
    }

    private Rectangle createRectangle(Coordinate coordinate, ArrayList<Coordinate> pivots) {
        Rectangle rect = new Rectangle(50, 30);
        rect.setStroke(Color.BLACK);
        rect.setStrokeWidth(2);
        rect.setFill(Color.TRANSPARENT);

        for (Coordinate p : pivots) {
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

    public boolean hasNextStep(SimplexTable simplexTable) {
        return !simplexTable.getPivots().isEmpty();
    }

    public boolean hasPrevStep(SimplexTable simplexTable) {
        return steps.size() != 1;
    }

    public void onNextButtonClick() {
        try {
            if (!hasStepAfterAB()) {
                currentStep++;
                SimplexTable nextTable = new SimplexTable(simplex, selectedPivot);
                this.simplex = nextTable;
                pivotLabel.setText("");
                table.getChildren().clear();
                lastSelectedRect = null;
                steps.add(nextTable);
                init(this.simplex);
            } else {
                currentStep++;
                SimplexTable fromABToSimplex = new SimplexTable(simplex);
                this.simplex = fromABToSimplex;
                pivotLabel.setText("");
                table.getChildren().clear();
                lastSelectedRect = null;
                steps.add(fromABToSimplex);
                init(this.simplex);
            }
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка");
            alert.setHeaderText("Произошла ошибка при обработке данных");
            alert.showAndWait();
            System.out.println(e.getMessage());
        }
    }

    public boolean hasStepAfterAB() {
        int lastRowIndex = this.simplex.getElements().length - 1;
        if (!this.simplex.isArtBasis()) return false;
        for (int j = 0; j < this.simplex.getFreeVariables().size() + 1; j++) {
            if (this.simplex.getElements()[lastRowIndex][j].getNumerator() != 0) {
                return false;
            }
        }
        return true;
    }

    public void onPrevButtonClick() {
        if (currentStep > 0) {
            basisLabel.setText("");
            currentStep--;
            SimplexTable prevTable = steps.get(currentStep);
            this.simplex = prevTable;
            pivotLabel.setText("");
            table.getChildren().clear();
            lastSelectedRect = null;
            if (currentStep < steps.size() - 1) {
                steps.remove(currentStep + 1);
            }
            init(this.simplex);
        }
    }
}
