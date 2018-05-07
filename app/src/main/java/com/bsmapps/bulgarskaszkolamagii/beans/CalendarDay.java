package com.bsmapps.bulgarskaszkolamagii.beans;

/**
 * Created by Mlody Danon on 7/25/2017.
 */

public class CalendarDay {

    private String day;
    private Long timestamp;

    public CalendarDay() {
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
