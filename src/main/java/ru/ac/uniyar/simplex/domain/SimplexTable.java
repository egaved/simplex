package ru.ac.uniyar.simplex.domain;

import org.apache.commons.lang3.math.Fraction;
import ru.ac.uniyar.simplex.secondary.Coordinate;


import java.util.ArrayList;


import static ru.ac.uniyar.simplex.calculations.Gauss.solveSystemWithBasis;

public class SimplexTable {

    private ArrayList<Integer> basicVariables;
    private ArrayList<Integer> freeVariables;
    private Fraction[][] elements;
    private Condition condition;
    private ArrayList<Coordinate> pivots;

    public SimplexTable() {

    }

    public SimplexTable(SimplexTable prevTable, Coordinate pivotCoordinates) {
        this.basicVariables = new ArrayList<>(prevTable.basicVariables);
        this.freeVariables = new ArrayList<>(prevTable.freeVariables);
        this.pivots = new ArrayList<>();

        swapVariables(pivotCoordinates);

        calculateMatrix(prevTable.getElements(), pivotCoordinates);
        findPivots(this);
    }

    public void calculateMatrix(Fraction[][] prevTable, Coordinate pivot) {
        int pri = pivot.getRowIndex(); // pri - pivot row index (номер строки, элементы которой будут изменяться)
        int pci = pivot.getColIndex();
        Fraction[][] newTable = new Fraction[prevTable.length][prevTable[0].length];

        for (int i = 0; i < prevTable.length; i++) {
            System.arraycopy(prevTable[i], 0, newTable[i], 0, prevTable[i].length);
        }

        newTable[pri][pci] = Fraction.getFraction("1").divideBy(prevTable[pri][pci]);

        //делим строку на опорный
        for (int j = 0; j < prevTable[0].length; j++) {
            if (j == pci) continue;
            newTable[pri][j] = newTable[pri][j].divideBy(prevTable[pri][pci]);
        }

        //делим столбец на минус опорный
        for (int i = 0; i < prevTable.length; i++) {
            if (i == pri) continue;
            Fraction negative = Fraction.getFraction(-1,1);
            newTable[i][pci] = newTable[i][pci].divideBy(prevTable[pri][pci].multiplyBy(negative));
        }

        for (int i = 0; i < newTable.length; i++) {
            if (i == pri) continue;
            for (int j = 0; j < newTable[0].length; j++) {
                if (j == pci) continue;
                newTable[i][j] = newTable[i][j].subtract(prevTable[i][pci].multiplyBy(newTable[pri][j]));
            }
        }
        this.elements = newTable;
    }

    public void swapVariables(Coordinate coordinate) {
        int freeNumIndex = coordinate.getColIndex();
        int basicNumIndex = coordinate.getRowIndex();

        int temp = basicVariables.get(basicNumIndex);
        basicVariables.set(basicNumIndex, freeVariables.get(freeNumIndex));
        freeVariables.set(freeNumIndex, temp);
    }

    public SimplexTable(Condition condition) {
        this.basicVariables = new ArrayList<>(condition.getBasis());
        this.freeVariables = new ArrayList<>(condition.getFreeVars());

        calculateMatrix(condition); // just given matrix after gauss.
        findPivots(this);
    }



    public void findPivots(SimplexTable simplexTable) {
        ArrayList<Integer> suitableColumns = findPivotColumns(simplexTable);
        ArrayList<Coordinate> coordinates = new ArrayList<>();

        for (int j : suitableColumns) {
            Fraction min = null;
            // поиск минимумального b/a
            for (int i = 0; i < simplexTable.getElements().length - 1; i++) {
                Fraction a = simplexTable.getElements()[i][j];
                if (a.getNumerator() <= 0) continue;
                Fraction b = simplexTable.getElements()[i][freeVariables.size()];
                Fraction bByCurr = b.divideBy(a);
                if (min == null) {
                    min = bByCurr;
                } else if (bByCurr.compareTo(min) <= 0) {
                    min = bByCurr;
                }
            }
            //сравнение каждого b/a с минимумом (на случай если несколько)
            for (int i = 0; i < simplexTable.getElements().length - 1; i++) {
                Fraction a = simplexTable.getElements()[i][j];
                if (a.getNumerator() <= 0) continue;
                Fraction b = simplexTable.getElements()[i][freeVariables.size()];
                Fraction bByCurr = b.divideBy(a);
                if (bByCurr.compareTo(min) == 0)
                    coordinates.add(new Coordinate(i, j));
            }
        }
        this.pivots = coordinates;
    }

    public ArrayList<Integer> findPivotColumns(SimplexTable simplexTable) {
        ArrayList<Integer> suitableColumns = new ArrayList<>();
        for (int j = 0; j <= simplexTable.getFreeVariables().size() - 1; j++) {
            Fraction fr = simplexTable.getElements()[simplexTable.getBasicVariables().size()][j];
            if (fr.getNumerator() < 0) {
                suitableColumns.add(j);
            }
        }
        return suitableColumns;
    }

    public void calculateMatrix(Condition condition) {
        int n = condition.getRestrictionsNum();
        int m = condition.getVariablesNum();
        Fraction[][] matrixBeforeGauss = new Fraction[n][m + 1];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m + 1; j++) {
                matrixBeforeGauss[i][j] = Fraction.getFraction(condition.getRestrictionsCoefficients()[i][j]);
            }
        }
        Fraction[][] gaussMatrix = solveSystemWithBasis(matrixBeforeGauss, condition.getBasis());
        Fraction[][] finalM = formElementsTable(condition, gaussMatrix);
        this.elements = finalM;
    }

    public Fraction[][] formElementsTable(Condition condition, Fraction[][] gaussMatrix) {
        int n = condition.getBasis().size();
        int m = condition.getFreeVars().size();
        Fraction[] target = new Fraction[n + m];

        for (int i = 0; i < target.length; i++) {
            target[i] = Fraction.getFraction(condition.getTargetFuncCoefficients()[i]);
        }

        Fraction[][] freeVarsTable = getFreeVarsMatrix(gaussMatrix, condition.getBasis(), condition.getFreeVars());
        Fraction[] lastRow = countLastRow(target, condition.getBasis(), condition.getFreeVars(), freeVarsTable);
        Fraction[][] finalSimplexTable = new Fraction[freeVarsTable.length + 1][freeVarsTable[0].length];

        // копируем данные из исходной матрицы
        for (int i = 0; i < freeVarsTable.length; i++) {
            System.arraycopy(freeVarsTable[i], 0, finalSimplexTable[i], 0, freeVarsTable[i].length);
        }
        // копируем данные из массива
        System.arraycopy(lastRow, 0, finalSimplexTable[freeVarsTable.length], 0, lastRow.length);

        return finalSimplexTable;
    }

    public Fraction[][] getFreeVarsMatrix(Fraction[][] gaussMatrix, ArrayList<Integer> basis, ArrayList<Integer> free) {
        int n = basis.size();
        int m = free.size() + 1;
        Fraction[][] fvm = new Fraction[n][m];

        for (int i = 0; i < gaussMatrix.length; i++) {
            int k = 0;
            for (int j = 0; j < gaussMatrix[0].length; j++) {
                if (basis.contains(j + 1)) continue;
                fvm[i][k] = gaussMatrix[i][j];
                k++;
            }
        }

        return fvm;
    }

    public Fraction[] countLastRow(Fraction[] target, ArrayList<Integer> basis, ArrayList<Integer> free, Fraction[][] matrix) {
        Fraction[] row = new Fraction[free.size() + 1];
        for (int i = 0; i < row.length; i++) {
            row[i] = Fraction.getFraction(0, 1);
        }
        Fraction negativeMultiplier = Fraction.getFraction(-1, 1);
        for (int j = 0; j < row.length; j++) {
            int k = 0;
            if (j != row.length - 1) {
                for (int i = 0; i < target.length; i++) {
                    int x_ = i + 1; // номер переменной
                    if (!basis.contains(x_)) { // если переменная не базисная
                        if (free.get(j) != x_) continue; // если рассматривается ЕЁ коэфф из целевой функции
                        Fraction oldValue = row[j];
                        Fraction valueToAdd = target[i];
                        Fraction sum = oldValue.add(valueToAdd);
                        row[j] = sum;
                    } else {
                        Fraction oldValue = row[j];
                        Fraction valueToAdd = matrix[k][j].multiplyBy(negativeMultiplier).multiplyBy(target[i]);
                        Fraction sum = oldValue.add(valueToAdd);
                        row[j] = sum;
                        k++;
                    }
                }
            } else {
                for (int i = 0; i < target.length; i++) {
                    int x_ = i + 1; // номер переменной
                    if (!basis.contains(x_)) continue;
                    Fraction oldValue = row[j];
                    Fraction valueToAdd = matrix[k][j].multiplyBy(negativeMultiplier).multiplyBy(target[i]);
                    Fraction sum = oldValue.add(valueToAdd);
                    row[j] = sum;
                    k++;
                }
            }
        }
        return row;
    }

    //----------------------------G&S-------------------------------
    public ArrayList<Integer> getBasicVariables() {
        return basicVariables;
    }

    public ArrayList<Integer> getFreeVariables() {
        return freeVariables;
    }

    public void setFreeVariables(ArrayList<Integer> freeVariables) {
        this.freeVariables = freeVariables;
    }

    public Fraction[][] getElements() {
        return elements;
    }

    public void setElements(Fraction[][] elements) {
        this.elements = elements;
    }

    public Condition getCondition() {
        return condition;
    }

    public void setCondition(Condition condition) {
        this.condition = condition;
    }

    public ArrayList<Coordinate> getPivots() {
        return pivots;
    }

    public void setPivots(ArrayList<Coordinate> pivots) {
        this.pivots = pivots;
    }
}
