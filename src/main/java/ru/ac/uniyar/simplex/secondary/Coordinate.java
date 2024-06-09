package ru.ac.uniyar.simplex.secondary;

public class Coordinate {

    private Integer rowIndex;
    private Integer colIndex;

    public Coordinate(Integer i, Integer j) {
        this.rowIndex = i;
        this.colIndex = j;
    }

    public Integer getColIndex() {
        return colIndex;
    }

    public void setColIndex(Integer colIndex) {
        this.colIndex = colIndex;
    }

    public Integer getRowIndex() {
        return rowIndex;
    }

    public void setRowIndex(Integer rowIndex) {
        this.rowIndex = rowIndex;
    }
}
