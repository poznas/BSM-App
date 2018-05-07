package com.bsmapps.bulgarskaszkolamagii.beans.shipment;

/**
 * Created by Mlody Danon on 7/26/2017.
 */

public class BetShipment {

    private boolean valid;
    private String info;
    private Long points;

    public BetShipment(String info, Long points) {
        this.info = info;
        this.points = points;
        this.valid = true;
    }

    public BetShipment() {
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public Long getPoints() {
        return points;
    }

    public void setPoints(Long points) {
        this.points = points;
    }
}
