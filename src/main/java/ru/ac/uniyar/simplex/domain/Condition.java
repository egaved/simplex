package ru.ac.uniyar.simplex.domain;

import org.apache.commons.lang3.math.Fraction;

import java.util.List;

public class Condition {
    private Integer variablesNum;

    private Integer restrictionsNum;

    private Boolean minimize;

    private Boolean decimals;

    private Boolean artificialBasis;

    private Fraction[] targetFuncCoefficients;

    private Fraction[][] restrictionsCoefficients;

    private List<Integer> basis;



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


    public List<Integer> getBasis() {
        return basis;
    }

    public void setBasis(List<Integer> basis) {
        this.basis = basis;
    }

    public Boolean getArtificialBasis() {
        return artificialBasis;
    }

    public void setArtificialBasis(Boolean artificialBasis) {
        this.artificialBasis = artificialBasis;
    }
}
