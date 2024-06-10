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
    private boolean artBasis = false;

    public SimplexTable() {

    }

    public SimplexTable(SimplexTable artBasTable) {
        this.setCondition(artBasTable.getCondition());
        this.basicVariables = new ArrayList<>(artBasTable.getBasicVariables());
        this.freeVariables = new ArrayList<>(artBasTable.getFreeVariables());
        this.pivots = new ArrayList<>();
        this.artBasis = false;

        int n = artBasTable.getBasicVariables().size();
        int m = artBasTable.getFreeVariables().size();
        this.elements = new Fraction[n + 1][m + 1];
        for (int i = 0; i < n + 1; i++) {
            for (int j = 0; j < m + 1; j++) {
                this.elements[i][j] = Fraction.getFraction(artBasTable.getElements()[i][j].toString());
            }
        }

        recountLastRow();
        findPivots(this);
    }

    public void recountLastRow() {
        int n = this.basicVariables.size();
        int m = this.freeVariables.size();
        Fraction[] target = new Fraction[n + m];
        for (int i = 0; i < target.length; i++)
            target[i] = Fraction.getFraction(this.condition.getTargetFuncCoefficients()[i]);
        Fraction[][] freeVarsMatrix = getFreeVarsMatrix(this.elements);
        Fraction[] lastRow = countLastRow(
                target,
                this.basicVariables,
                this.freeVariables,
                freeVarsMatrix
        );

        for (int j = 0; j < m + 1; j++) {
            this.elements[n][j] = lastRow[j];
        }
    }

    public static Fraction[][] getFreeVarsMatrix(Fraction[][] array) {
        int n = array.length;
        int m = array[0].length;

        Fraction[][] newArray = new Fraction[n - 1][m];

        for (int i = 0; i < n - 1; i++) {
            System.arraycopy(array[i], 0, newArray[i], 0, m);
        }

        return newArray;
    }

    public SimplexTable(SimplexTable prevTable, Coordinate pivotCoordinates) {
        this.setCondition(prevTable.getCondition());
        this.basicVariables = new ArrayList<>(prevTable.basicVariables);
        this.freeVariables = new ArrayList<>(prevTable.freeVariables);
        this.pivots = new ArrayList<>();
        this.artBasis = prevTable.artBasis;

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
            Fraction negative = Fraction.getFraction(-1, 1);
            newTable[i][pci] = newTable[i][pci].divideBy(prevTable[pri][pci].multiplyBy(negative));
        }

        for (int i = 0; i < newTable.length; i++) {
            if (i == pri) continue;
            for (int j = 0; j < newTable[0].length; j++) {
                if (j == pci) continue;
                newTable[i][j] = newTable[i][j].subtract(prevTable[i][pci].multiplyBy(newTable[pri][j]));
            }
        }

        //удаление столбца
        if (this.artBasis) {
            newTable = removeColumn(newTable, pci);
            freeVariables.remove(pci);
        }

        this.elements = newTable;
    }

//    public boolean checkOn

    public Fraction[][] removeColumn(Fraction[][] array, int columnIndex) {
        int n = array.length;
        int m = array[0].length;

        // Создаем новый массив с размерностью [n][m-1]
        Fraction[][] newArray = new Fraction[n][m - 1];

        // Копируем в новый массив все столбцы, кроме того, который нужно удалить
        for (int i = 0; i < n; i++) {
            System.arraycopy(array[i], 0, newArray[i], 0, columnIndex);
            System.arraycopy(array[i], columnIndex + 1, newArray[i], columnIndex, m - columnIndex - 1);
        }

        return newArray;
    }

    public void swapVariables(Coordinate coordinate) {
        int freeNumIndex = coordinate.getColIndex();
        int basicNumIndex = coordinate.getRowIndex();

        int temp = basicVariables.get(basicNumIndex);
        basicVariables.set(basicNumIndex, freeVariables.get(freeNumIndex));
        freeVariables.set(freeNumIndex, temp);
    }

    public SimplexTable(Condition condition) {
        for (int i = 0; i < condition.getRestrictionsCoefficients().length; i++) {
            int lastCol = condition.getRestrictionsCoefficients()[0].length;
            Fraction negativeMultiplier = Fraction.getFraction(-1, 1);
            Fraction b = Fraction.getFraction(condition.getRestrictionsCoefficients()[i][lastCol - 1]);
            if (b.getNumerator() < 0) {
                for (int j = 0; j < lastCol; j++) {
                    Fraction fr = Fraction.getFraction(condition.getRestrictionsCoefficients()[i][j])
                            .multiplyBy(negativeMultiplier);
                    condition.getRestrictionsCoefficients()[i][j] = fr.toString();
                }
            }
        }

        if (!condition.getArtificialBasis()) {
            this.setCondition(condition);
            artBasis = false;
            this.basicVariables = new ArrayList<>(condition.getBasis());
            this.freeVariables = new ArrayList<>(condition.getFreeVars());

            calculateMatrix(condition); // just given matrix after gauss.
            findPivots(this);
        } else {
            this.setCondition(condition);
            artBasis = true;
            ArrayList<Integer> artBasFree = new ArrayList<>();
            ArrayList<Integer> artBasBas = new ArrayList<>();

            for (int i = 1; i <= condition.getVariablesNum() + condition.getRestrictionsNum(); i++) {
                if (i <= condition.getVariablesNum())
                    artBasFree.add(i);
                else
                    artBasBas.add(i);
            }
            condition.setBasis(artBasBas);
            condition.setFreeVars(artBasFree);
            this.basicVariables = new ArrayList<>(condition.getBasis());
            this.freeVariables = new ArrayList<>(condition.getFreeVars());

            calculateMatrix(condition); // just given matrix after gauss.
            findPivots(this);
        }
    }


    public void findPivots(SimplexTable simplexTable) {
        ArrayList<Integer> suitableColumns = findPivotColumns(simplexTable);
        ArrayList<Coordinate> coordinates = new ArrayList<>();

        for (int j : suitableColumns) {
            Fraction min = null;
            // поиск минимального b/a
            for (int i = 0; i < simplexTable.getElements().length - 1; i++) {
                int x_ = simplexTable.getBasicVariables().get(i); // номер рассматриваемой переменной
                // при искуственном базисе пропускать строки переменных из условия.
                if (artBasis && simplexTable.getCondition().getFreeVars().contains(x_)) continue;

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
        Fraction[][] finalM;
        if (!artBasis) {
            Fraction[][] gaussMatrix = solveSystemWithBasis(matrixBeforeGauss, condition.getBasis());
            finalM = formElementsTable(condition, gaussMatrix);
        } else {
            for (int i = 0; i < n; i++) {
                if (matrixBeforeGauss[i][m].getNumerator() < 0) {
                    for (int j = 0; j < m; j++) {
                        matrixBeforeGauss[i][j] = matrixBeforeGauss[i][j].multiplyBy(Fraction.getFraction("-1"));
                    }
                }
            }
            finalM = formElementsTable(condition, matrixBeforeGauss);
        }

        this.elements = finalM;
    }

    public Fraction[][] formElementsTable(Condition condition, Fraction[][] gaussMatrix) {
        int n = condition.getBasis().size();
        int m = condition.getFreeVars().size();
        Fraction[] target;
        if (!artBasis) {
            target = new Fraction[n + m];
            for (int i = 0; i < target.length; i++)
                target[i] = Fraction.getFraction(condition.getTargetFuncCoefficients()[i]);

        } else {
            target = new Fraction[n];
            for (int i = 0; i < target.length; i++) {
                target[i] = Fraction.getFraction(1, 1);
            }
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
                if (!artBasis && basis.contains(j + 1)) continue;
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
            if (j != row.length - 1) {
                if (artBasis) {
                    for (int i = 0; i < basis.size(); i++) {
                        Fraction ov = row[j];
                        Fraction vta = matrix[i][j].multiplyBy(negativeMultiplier);
                        Fraction sum = ov.add(vta);
                        row[j] = sum;
                    }
                } else {
                    for (int i = 0; i < basis.size(); i++) {
                        int x_ = basis.get(i);
                        Fraction ov = row[j];
                        Fraction vta = matrix[i][j].multiplyBy(negativeMultiplier).multiplyBy(target[x_ - 1]);
                        Fraction s = ov.add(vta);
                        row[j] = s;
                    }
                    for (int x_ : free) {
                        if (free.get(j) != x_) continue;
                        Fraction ov = row[j];
                        Fraction vta = target[x_ - 1];
                        Fraction s = ov.add(vta);
                        row[j] = s;
                    }
                }
            } else {
                if (artBasis) {
                    for (int i = 0; i < basis.size(); i++) {
                        Fraction ov = row[j];
                        Fraction vta = matrix[i][j].multiplyBy(negativeMultiplier);
                        Fraction sum = ov.add(vta);
                        row[j] = sum;
                    }
                } else {
                    for (int i = 0; i < basis.size(); i++) {
                        int x_ = basis.get(i);
                        Fraction ov = row[j];
                        Fraction vta = matrix[i][j].multiplyBy(target[x_ - 1]);
                        Fraction sum = ov.add(vta);
                        row[j] = sum;
                    }
                    row[j] = row[j].multiplyBy(negativeMultiplier);
                }
            }
        }
        return row;
    }

    public boolean isUnbounded() {
        if(artBasis) return false;
        for (int j = 0; j < this.elements[0].length; j++) {
            boolean negative = true;
            for (Fraction[] element : this.elements) {
                if (element[j].getNumerator() > 0) {
                    negative = false;
                    break;
                }
            }
            if (negative) {
                return true;
            }
        }
        return false;
    }

    public boolean hasNoFreeVars() {
        return this.getFreeVariables().isEmpty();
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

    public boolean isArtBasis() {
        return artBasis;
    }

    public void setArtBasis(boolean artBasis) {
        this.artBasis = artBasis;
    }
}
