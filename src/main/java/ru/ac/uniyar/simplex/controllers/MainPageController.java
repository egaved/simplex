package ru.ac.uniyar.simplex.controllers;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

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


    public void onSubmitButtonClicked() {
        try {
            int columns = Integer.parseInt(variablesNum.getText());
            int rows = Integer.parseInt(restrictionsNum.getText());
            if (columns < 2 || rows < 1 || rows >= columns)
                throw new NumberFormatException();
            table.getChildren().clear();
//            table.setPrefWidth(columns * 50);
//            table.setPrefHeight(rows * 50);

            // Создание заголовков столбцов
            for (int i = 0; i <= columns + 1; i++) {
                String columnHeader;
                if (i == 0) columnHeader = " ";
                else if (i == columns + 1) columnHeader = "     b";
                else columnHeader = "   a" + (i);
                Label columnLabel = new Label(columnHeader);
                table.add(columnLabel, i, 0);
            }



            for (int i = 1; i <= rows + 1; i++) {
                table.addRow(i);
                for (int j = 0; j <= columns + 1; j++) {
                    if (j == 0) {
                        String rowHeader = "f" + (i - 1) + "(x)";
                        Label rowLabel = new Label(rowHeader);
                        table.add(rowLabel, 0, i); // Добавляем заголовок строки
                    }
                    else {
                        TextField tf = new TextField("0");
                        table.add(tf,j,i);
                    }
                }
            }

            List<Integer> values = getTableValues();
            System.out.println(values);
        } catch (NumberFormatException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка");
            alert.setHeaderText("Произошла ошибка при обработке данных");
            alert.setContentText(readFile());
            alert.showAndWait();
        }
    }

    public List<Integer> getTableValues() {
        List<Integer> values = new ArrayList<>();
        for (Node node : table.getChildren()) {
            if (node instanceof TextField) {
                values.add(Integer.valueOf(((TextField) node).getText()));
            }
        }
        return values;
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
