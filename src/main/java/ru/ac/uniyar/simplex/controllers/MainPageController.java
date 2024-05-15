package ru.ac.uniyar.simplex.controllers;

import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import org.apache.commons.lang3.math.Fraction;
import ru.ac.uniyar.simplex.domain.Condition;
import ru.ac.uniyar.simplex.stages.SimplexStepsStage;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
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
    private GridPane table;
    @FXML
    private Button saveButton;

    private Condition condition;

    public void onSubmitButtonClicked() {
        try {
            int columns = Integer.parseInt(variablesNum.getText());
            int rows = Integer.parseInt(restrictionsNum.getText());
            if (columns < 2 || rows < 1 || rows >= columns)
                throw new NumberFormatException();
            table.getChildren().clear();

            // Создание заголовков столбцов
            for (int i = 0; i <= columns + 1; i++) {
                String columnHeader;
                if (i == 0) columnHeader = " ";
                else if (i == columns + 1) columnHeader = "     b";
                else columnHeader = "   a" + (i);
                Label columnLabel = new Label(columnHeader);
                table.add(columnLabel, i, 0);
                GridPane.setHalignment(columnLabel, HPos.CENTER);
            }

            for (int i = 1; i <= rows + 1; i++) {
                table.addRow(i);
                for (int j = 0; j <= columns + 1; j++) {
                    if (j == 0) {
                        String rowHeader = "f" + (i - 1) + "(x)";
                        Label rowLabel = new Label(rowHeader);
                        table.add(rowLabel, 0, i); // Добавляем заголовок строки
                    } else {
                        TextField tf = new TextField("0");
                        table.add(tf, j, i);
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

    public Fraction[] getTargetFuncCoefficients() {
        Fraction[] values = new Fraction[Integer.parseInt(variablesNum.getText())];
        int i = 0;
        for (Node node : table.getChildren()) {
            if (GridPane.getRowIndex(node) == 0 && node instanceof TextField) {
                Fraction fraction = Fraction.getFraction(((TextField) node).getText());
                if (fraction != null) {
                    values[i++] = fraction;
                }
            }
        }
        return values;
    }

    public Fraction[][] getRestrictionsCoefficients() {
        int n = Integer.parseInt(restrictionsNum.getText());
        int m = Integer.parseInt(variablesNum.getText());
        Fraction[][] values = new Fraction[n][m];
        int i = 0;
        int j = 0;
        for (Node node : table.getChildren()) {
            if (GridPane.getRowIndex(node) == 0 && node instanceof TextField) {
                Fraction fraction = Fraction.getFraction(((TextField) node).getText());
                if (fraction != null) {
                    values[i++][j++] = fraction;
                }
            }
        }
        return values;
    }

    public void onSaveButtonClicked() {
        this.condition = new Condition();
        condition.setVariablesNum(Integer.parseInt(variablesNum.getText()));
        condition.setRestrictionsNum(Integer.parseInt(restrictionsNum.getText()));
        condition.setMinimize(taskCB.getValue().equals("Минимизировать"));
        condition.setDecimals(fractionsCB.getValue().equals("Десятичные"));
        condition.setTargetFuncCoefficients(getTargetFuncCoefficients());
        condition.setRestrictionsCoefficients(getRestrictionsCoefficients());

        SimplexStepsStage simplexStage = new SimplexStepsStage();
        simplexStage.display(condition);
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
