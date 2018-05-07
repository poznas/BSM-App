package com.bsmapps.bulgarskaszkolamagii.beans;

/**
 * Created by Mlody Danon on 7/21/2017.
 */

public class Privilege {

    private int iconId;
    private String brand;
    private boolean checkIfContain;
    private int pendingReports;

    public Privilege( int icon, String brand ){
        this.brand = brand;
        this.iconId = icon;
        checkIfContain = false;
        pendingReports = -1;
    }

    public Privilege( int icon, String brand, boolean check ){
        this.brand = brand;
        this.iconId = icon;
        checkIfContain = check;
        pendingReports = -1;
    }

    public int getIconId() {
        return iconId;
    }

    public void setIconId(int iconId) {
        this.iconId = iconId;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public boolean isCheckIfContain() {
        return checkIfContain;
    }

    public void setCheckIfContain(boolean checkIfContain) {
        this.checkIfContain = checkIfContain;
    }

    public int getPendingReports() {
        return pendingReports;
    }

    public void setPendingReports(int pendingReports) {
        this.pendingReports = pendingReports;
    }
}
