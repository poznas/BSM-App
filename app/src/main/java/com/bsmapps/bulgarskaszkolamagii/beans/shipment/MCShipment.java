package com.bsmapps.bulgarskaszkolamagii.beans.shipment;

/**
 * Created by Mlody Danon on 7/26/2017.
 */

public class MCShipment {

    private boolean valid;
    private Long points;
    private String team;
    private String name;
    private String info;

    public MCShipment() {
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public MCShipment(Long points, String team, String name, String info) {
        this.points = points;
        this.team = team;
        this.name = name;
        this.info = info;
        this.valid = true;
    }
}
