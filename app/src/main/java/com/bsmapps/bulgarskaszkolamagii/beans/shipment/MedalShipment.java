package com.bsmapps.bulgarskaszkolamagii.beans.shipment;

/**
 * Created by Mlody Danon on 7/27/2017.
 */

public class MedalShipment {

    private Long points;
    private String team;
    private String info;
    private boolean valid;

    public MedalShipment() {
    }

    public MedalShipment(Long points, String team, String info) {
        this.points = points;
        this.team = team;
        this.info = info;
        this.valid = true;
    }

    public Long getPoints() {
        return points;
    }

    public void setPoints(Long points) {
        this.points = points;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }
}
