package ru.ac.uniyar.simplex.domain;

import org.apache.commons.lang3.math.Fraction;

public class Condition {
    private Integer variablesNum;

    private Integer restrictionsNum;

    private Boolean minimize;

    private Boolean decimals;

    private Fraction[] targetFuncCoefficients;

    private Fraction[][] restrictionsCoefficients;

//    public Condition () {
//        this.variablesNum = null;
//        this.restrictionsNum = null;
//        this.minimize = null;
//        this.decimals = null;
//        this.targetFuncCoefficients = null;
//        this.restrictionsCoefficients = null;
//    }
//
//    public Condition(Integer variablesNum, Integer restrictionsNum, String task, String fractions, Fraction[] targetFuncCoefficients, Fraction[][] restrictionsCoefficients) {
//        this.variablesNum = variablesNum;
//        this.restrictionsNum = restrictionsNum;
//
//        this.minimize = task.equals("Минимизировать");
//        this.decimals = fractions.equals("Десятичные");
//
//        this.targetFuncCoefficients = targetFuncCoefficients;
//        this.restrictionsCoefficients = restrictionsCoefficients;
//    }

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

    public Fraction[] getTargetFuncCoefficients() {
        return targetFuncCoefficients;
    }

    public void setTargetFuncCoefficients(Fraction[] targetFuncCoefficients) {
        this.targetFuncCoefficients = targetFuncCoefficients;
    }

    public Fraction[][] getRestrictionsCoefficients() {
        return restrictionsCoefficients;
    }

    public void setRestrictionsCoefficients(Fraction[][] restrictionsCoefficients) {
        this.restrictionsCoefficients = restrictionsCoefficients;
    }
}
