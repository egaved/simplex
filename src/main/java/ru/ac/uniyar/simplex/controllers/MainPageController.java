package ru.ac.uniyar.simplex.controllers;

import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import org.apache.commons.lang3.math.Fraction;
import ru.ac.uniyar.simplex.domain.Condition;
import ru.ac.uniyar.simplex.windows.SimplexStepsWindow;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import static ru.ac.uniyar.simplex.secondary.JSONReader.readTaskFromJSON;
import static ru.ac.uniyar.simplex.secondary.JSONReader.saveTaskToJSONFile;

public class MainPageController {

    @FXML
    private TextField variablesNum;
    @FXML
    private TextField restrictionsNum;
    @FXML
    private ChoiceBox<String> taskCB;
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

    public String[] getTargetFuncCoefficients() throws Exception {
        String[] values = new String[Integer.parseInt(variablesNum.getText())];
        int i = 0;
        for (Node node : targetTable.getChildren()) {
            if (node instanceof TextField textField) {
                String value = textField.getText();
                if (value.equals("0") || value.isEmpty())
                    throw new Exception("Поля коэффициентов целевой функции не могут содержать нули или быть пустыми.");
                Fraction fraction = Fraction.getFraction(value);
                if (fraction != null) {
//                    if (fraction.getDenominator() == 1)
//                        values[i] = String.valueOf(fraction.getNumerator());
//                    else
                    values[i] = String.valueOf(fraction.reduce());
                }
                i++;
            }
        }
        return values;
    }

    public String[][] getRestrictionsCoefficients() throws Exception {
        int n = Integer.parseInt(restrictionsNum.getText()); //restrictions
        int m = Integer.parseInt(variablesNum.getText());   //vars
        String[][] values = new String[n][];
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

                    values[i] = new String[row.size()];

                    for (int j = 0; j < row.size(); j++) {
                        Fraction fraction = Fraction.getFraction(row.get(j)).reduce();
                        if (fraction.getDenominator() == 1) values[i][j] = String.valueOf(fraction.getNumerator());
                        else values[i][j] = String.valueOf(fraction);
                    }
                    row.clear();
                    nz = false;
                    i++;
                }
            }
        }
        return values;
    }

    private ArrayList<Integer> getBasisVars() throws Exception {
        ArrayList<Integer> basis = new ArrayList<>();
        for (Node node : targetTable.getChildren()) {
            if (node instanceof CheckBox checkBox && checkBox.isSelected())
                basis.add(GridPane.getColumnIndex(checkBox));
        }
        if (!basis.isEmpty() && basis.size() < Integer.parseInt(restrictionsNum.getText()))
            throw new Exception("Количество базисов должно соответствовать количеству ограничений.");
        return basis;
    }

    public ArrayList<Integer> getFreeVars(Condition condition) {
        ArrayList<Integer> vars = new ArrayList<>();
        for (int i = 1; i <= condition.getVariablesNum(); i++) {
            if (!(condition.getBasis().contains(i)))
                vars.add(i);
        }
        return vars;
    }

    public void onSaveButtonClicked() throws Exception {
        try {
            this.condition = new Condition();
            condition.setVariablesNum(Integer.parseInt(variablesNum.getText()));
            condition.setRestrictionsNum(Integer.parseInt(restrictionsNum.getText()));
            condition.setMinimize(taskCB.getValue().equals("Минимизировать"));
            condition.setTargetFuncCoefficients(getTargetFuncCoefficients());
            condition.setRestrictionsCoefficients(getRestrictionsCoefficients());
            ArrayList<Integer> basis = getBasisVars();
            if (basis.isEmpty()) {
                condition.setBasis(basis);
                condition.setArtificialBasis(true);
            } else {
                condition.setBasis(basis);
                condition.setFreeVars(getFreeVars(condition));
                condition.setArtificialBasis(false);
            }

            SimplexStepsWindow simplexStage = new SimplexStepsWindow();
            simplexStage.display(condition);
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка");
            alert.setHeaderText("Произошла ошибка при обработке данных");
            if (e.getClass().equals(NullPointerException.class))
                alert.setContentText("Не заданы условия задачи.");
            else
            if (e.getClass().equals(NumberFormatException.class))
                alert.setContentText("В одно из полей введено не число.");
            else
                alert.setContentText(e.toString());
            alert.showAndWait();
            System.out.println(e.getMessage());
        }
    }

    public void onSaveToFileButtonClicked() throws IOException {
        saveTaskToJSONFile(condition);
    }

    public void onReadFileButtonClicked() throws IOException {
        Condition condition = readTaskFromJSON();
        SimplexStepsWindow simplexStage = new SimplexStepsWindow();
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
