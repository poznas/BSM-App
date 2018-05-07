package com.bsmapps.bulgarskaszkolamagii.beans.sidemission;

import java.util.List;

/**
 * Created by Mlody Danon on 8/8/2017.
 */

public class ProperityDetails {

    private String name;
    private String hint;
    private String symbol;
    private String type;

    private List<String> spinnerKeys;
    private List<Long> spinnerValues;
    private Long limitedValue;
    private String profType;


    public ProperityDetails(String name, String hint, String symbol, String type) {
        this.name = name;
        this.hint = hint;
        this.symbol = symbol;
        this.type = type;

        spinnerKeys = null;
        spinnerValues = null;
        limitedValue = null;
        profType = null;
    }

    public List<String> getSpinnerKeys() {
        return spinnerKeys;
    }

    public void setSpinnerKeys(List<String> spinnerKeys) {
        this.spinnerKeys = spinnerKeys;
    }

    public List<Long> getSpinnerValues() {
        return spinnerValues;
    }

    public void setSpinnerValues(List<Long> spinnerValues) {
        this.spinnerValues = spinnerValues;
    }

    public Long getLimitedValue() {
        return limitedValue;
    }

    public void setLimitedValue(Long limitedValue) {
        this.limitedValue = limitedValue;
    }

    public String getProfType() {
        return profType;
    }

    public void setProfType(String profType) {
        this.profType = profType;
    }

    public ProperityDetails() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
