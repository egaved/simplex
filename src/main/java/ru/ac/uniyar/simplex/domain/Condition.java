package ru.ac.uniyar.simplex.domain;

import org.apache.commons.lang3.math.Fraction;

import java.util.ArrayList;
import java.util.List;

public class Condition {
    private Integer variablesNum;

    private Integer restrictionsNum;

    private Boolean minimize;

    private Boolean decimals;

    private Boolean artificialBasis;

    private String[] targetFuncCoefficients;

    private String[][] restrictionsCoefficients;

    private ArrayList<Integer> basis;

    private ArrayList<Integer> freeVars;

    public Integer getVariablesNum() {
        return variablesNum;
    }

    public void setVariablesNum(Integer variablesNum) {
        this.variablesNum = variablesNum;
    }

    public Integer getRestrictionsNum() {
        return restrictionsNum;
    }

    public void setRestrictionsNum(Integer restrictionsNum) {
        this.restrictionsNum = restrictionsNum;
    }

    public Boolean getMinimize() {
        return minimize;
    }

    public void setMinimize(Boolean minimize) {
        this.minimize = minimize;
    }

    public Boolean getDecimals() {
        return decimals;
    }

    public void setDecimals(Boolean decimals) {
        this.decimals = decimals;
    }

    public String[] getTargetFuncCoefficients() {
        return targetFuncCoefficients;
    }

    public void setTargetFuncCoefficients(String[] targetFuncCoefficients) {
        this.targetFuncCoefficients = targetFuncCoefficients;
    }

    public String[][] getRestrictionsCoefficients() {
        return restrictionsCoefficients;
    }

    public void setRestrictionsCoefficients(String[][] restrictionsCoefficients) {
        this.restrictionsCoefficients = restrictionsCoefficients;
    }


    public ArrayList<Integer> getBasis() {
        return basis;
    }

    public void setBasis(ArrayList<Integer> basis) {
        this.basis = basis;
    }

    public Boolean getArtificialBasis() {
        return artificialBasis;
    }

    public void setArtificialBasis(Boolean artificialBasis) {
        this.artificialBasis = artificialBasis;
    }

    public ArrayList<Integer> getFreeVars() {
        return freeVars;
    }

    public void setFreeVars(ArrayList<Integer> freeVars) {
        this.freeVars = freeVars;
    }
}
