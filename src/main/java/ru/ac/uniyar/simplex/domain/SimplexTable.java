package ru.ac.uniyar.simplex.domain;

import org.apache.commons.lang3.math.Fraction;


import java.util.ArrayList;
import java.util.Arrays;

import static java.lang.Math.abs;
import static ru.ac.uniyar.simplex.calculations.Gauss.solveSystemWithBasis;

public class SimplexTable {

    private final ArrayList<Integer> basicVariables;
    private ArrayList<Integer> freeVariables;
    private Fraction[][] elements;
    private Condition condition;

    public SimplexTable(Condition condition) {
        this.basicVariables = new ArrayList<>(condition.getBasis());
        this.freeVariables = new ArrayList<>(condition.getFreeVars());
        calculateMatrix(condition); // just given matrix after gauss.

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
        System.out.println(Arrays.deepToString(finalM));
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

        // Копируем данные из исходной матрицы
        for (int i = 0; i < freeVarsTable.length; i++) {
            System.arraycopy(freeVarsTable[i], 0, finalSimplexTable[i], 0, freeVarsTable[i].length);
        }
        // Копируем данные из массива
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
                    if (!basis.contains(x_) ) { // если переменная не базисная
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
}
