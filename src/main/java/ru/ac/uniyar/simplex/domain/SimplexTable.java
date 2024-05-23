package ru.ac.uniyar.simplex.domain;

import org.apache.commons.lang3.math.Fraction;


import java.util.ArrayList;
import java.util.Arrays;

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
        System.out.println(Arrays.deepToString(matrixBeforeGauss));
        this.elements = solveSystemWithBasis(matrixBeforeGauss, condition.getBasis());
    }

    public void formElementsTable(Condition condition, Fraction[][] gaussMatrix) {
        int n = condition.getBasis().size();
        int m = condition.getFreeVars().size();
        Fraction[] target = new Fraction[n + m];
        for (String string : condition.getTargetFuncCoefficients())
            for (int i = 0; i < target.length; i++)
                target[i] = Fraction.getFraction(string);

        Fraction[][] freeVarsTable = getFreeVarsMatrix(gaussMatrix, condition.getBasis(), condition.getFreeVars());
        Fraction[] lastRow = countLastRow(target, condition.getBasis(), condition.getFreeVars(), )
        Fraction[][] finalSimplexTable = new Fraction[n + 1][m + 1];
    }

    public Fraction[][] getFreeVarsMatrix(Fraction[][] gaussMatrix, ArrayList<Integer> basis, ArrayList<Integer> free) {
        int n = basis.size();
        int m = free.size() + 1;
        Fraction[][] fvm = new Fraction[n][m];

        for (int i = 0; i < gaussMatrix.length; i++) {
            for (int j = 0; j < gaussMatrix[0].length; j++) {
                if (basis.contains(j + 1)) continue;
                fvm[i][j - (m + 1)] = gaussMatrix[i][j];
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
            for (int i = 0; i < target.length; i++) {
                if (basis.contains(i)) continue;
                row[j].add(matrix[].multiplyBy(negativeMultiplier).multiplyBy(target[i]));
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
