package ru.ac.uniyar.simplex.controllers;

import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import org.apache.commons.lang3.math.Fraction;
import ru.ac.uniyar.simplex.domain.Condition;
import ru.ac.uniyar.simplex.exceptions.OnlyZerosException;
import ru.ac.uniyar.simplex.windows.SimplexStepsWindow;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainPageController {

    @FXML
    private TextField variablesNum;
    @FXML
    private TextField restrictionsNum;
    @FXML
    private ChoiceBox<String> taskCB;
    @FXML
    private ChoiceBox<String> fractionsCB;
    @FXML
    private GridPane targetTable;
    @FXML
    private GridPane restrictTable;
    @FXML
    private Button saveButton;

    private Condition condition;

    public void onSubmitButtonClicked() {
        try {
            int columns = Integer.parseInt(variablesNum.getText());
            int rows = Integer.parseInt(restrictionsNum.getText());
            if (columns < 2 || rows < 1 || rows >= columns)
                throw new NumberFormatException();
            targetTable.getChildren().clear();
            restrictTable.getChildren().clear();

            // Создание заголовков столбцов
            for (int i = 0; i <= columns; i++) {
                String columnHeader;
                if (i == 0) columnHeader = " ";
                else columnHeader = "x" + i;
                Label columnLabel = new Label(columnHeader);
                targetTable.add(columnLabel, i, 0);
                GridPane.setHalignment(columnLabel, HPos.CENTER);
            }

            for (int j = 0; j < columns + 1; j++) {
                if (j == 0) {
                    Label rowLabel = new Label("f(x)");
                    targetTable.add(rowLabel, j, 1);
                    GridPane.setHalignment(rowLabel, HPos.CENTER);
                } else {
                    TextField tf = new TextField("0");
                    tf.setPrefWidth(50);
                    targetTable.add(tf, j, 1);
                }
            }

            for (int j = 0; j <= columns; j++) {
                if (j == 0) {
                    Label label = new Label("Базис:");
                    targetTable.add(label, j, 2);
                } else {
                    CheckBox cb = new CheckBox();
                    targetTable.add(cb, j, 2);
                    GridPane.setHalignment(cb, HPos.CENTER);
                }
            }

            for (int i = 0; i <= columns + 1; i++) {
                String columnHeader;
                if (i == 0) columnHeader = " ";
                else if (i == columns + 1) columnHeader = "b";
                else columnHeader = "a" + i;
                Label columnLabel = new Label(columnHeader);
                restrictTable.add(columnLabel, i, 0);
                GridPane.setHalignment(columnLabel, HPos.CENTER);
            }

            for (int i = 1; i <= rows; i++) {
                restrictTable.addRow(i);
                for (int j = 0; j <= columns + 1; j++) {
                    if (j == 0) {
                        String rowHeader = "f" + i + "(x)";
                        Label rowLabel = new Label(rowHeader);
                        restrictTable.add(rowLabel, 0, i); // Добавляем заголовок строки
                    } else {
                        TextField tf = new TextField("0");
                        tf.setPrefWidth(50);
                        restrictTable.add(tf, j, i);
                    }
                }
            }


            saveButton.setVisible(true);

        } catch (NumberFormatException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка");
            alert.setHeaderText("Произошла ошибка при обработке данных");
            alert.setContentText(readFile());
            alert.showAndWait();
        }
    }

    public Fraction[] getTargetFuncCoefficients() throws Exception {
        Fraction[] values = new Fraction[Integer.parseInt(variablesNum.getText())];
        int i = 0;
        for (Node node : targetTable.getChildren()) {
            if (node instanceof TextField textField) {
                String value = textField.getText();
                if(value.equals("0") || value.isEmpty())
                    throw new Exception("Поля коэффициентов целевой функции не могут содержать нули или быть пустыми.");
                Fraction fraction = Fraction.getFraction(value);
                if (fraction != null) {
                    values[i] = fraction.reduce();
                }
                i++;
            }
        }
        return values;
    }

    public Fraction[][] getRestrictionsCoefficients() throws Exception {
        int n = Integer.parseInt(restrictionsNum.getText()); //restrictions
        int m = Integer.parseInt(variablesNum.getText());   //vars
        Fraction[][] values = new Fraction[n][];
        ArrayList<String> row = new ArrayList<>();
        int i = 0; //row
        boolean nz = false;
        for (Node node : restrictTable.getChildren()) {
            if (node instanceof TextField textField) {
                String value = textField.getText();
                row.add(value);
                if (!value.equals("0")) nz = true;
                if (row.size() == (m + 1)) {
                    if (!nz)
                        throw new Exception("Обнаружено ограничение, в котором все коэффициенты равны нулю.");

                    values[i] = new Fraction[row.size()];

                    for (int j = 0; j < row.size(); j++) {
                        values[i][j] = Fraction.getFraction(row.get(j));
                    }
                    row.clear();
                    nz = false;
                    i++;
                }
            }
        }
        return values;
    }

    private List<Integer> getBasisVars() throws Exception {
        List<Integer> basis = new ArrayList<>();
        for (Node node : targetTable.getChildren()) {
            if (node instanceof CheckBox checkBox && checkBox.isSelected())
                basis.add(GridPane.getColumnIndex(checkBox));
        }
        if (!basis.isEmpty() && basis.size() < Integer.parseInt(restrictionsNum.getText()))
            throw new Exception("Количество базисов должно соответствовать количеству ограничений.");
        return basis;
    }

    public void onSaveButtonClicked() {
        try {
            this.condition = new Condition();
            condition.setVariablesNum(Integer.parseInt(variablesNum.getText()));
            condition.setRestrictionsNum(Integer.parseInt(restrictionsNum.getText()));
            condition.setMinimize(taskCB.getValue().equals("Минимизировать"));
            condition.setDecimals(fractionsCB.getValue().equals("Десятичные"));
            condition.setTargetFuncCoefficients(getTargetFuncCoefficients());
            condition.setRestrictionsCoefficients(getRestrictionsCoefficients());
            List<Integer> basis = getBasisVars();
            if (basis.isEmpty()) {
                condition.setBasis(basis);
                condition.setArtificialBasis(true);
            } else {
                condition.setBasis(basis);
                condition.setArtificialBasis(false);
            }
            systemOutput(condition);
            SimplexStepsWindow simplexStage = new SimplexStepsWindow();
            simplexStage.display(condition);
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка");
            alert.setHeaderText("Произошла ошибка при обработке данных");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }

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


    public String readFile() {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader("C:\\Programming\\java\\projects\\simplex\\src\\main\\resources\\values-restrictions.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (IOException e) {
            System.err.println("Ошибка при чтении файла text.txt: " + e.getMessage());
        }
        return sb.toString();
    }
}
