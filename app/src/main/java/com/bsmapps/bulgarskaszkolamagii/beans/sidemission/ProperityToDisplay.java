package com.bsmapps.bulgarskaszkolamagii.beans.sidemission;

/**
 * Created by Mlody Danon on 7/29/2017.
 */

public class ProperityToDisplay {

    private String name;
    private Double grade;

    public ProperityToDisplay(String name, Double grade) {
        this.name = name;
        this.grade = grade;
    }

    public ProperityToDisplay() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getGrade() {
        return grade;
    }

    public void setGrade(Double grade) {
        this.grade = grade;
    }
}
