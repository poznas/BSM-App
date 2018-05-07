package com.bsmapps.bulgarskaszkolamagii.beans;

/**
 * Created by Mlody Danon on 7/25/2017.
 */

public class PointsInfo {

    private Long points;
    private String team;
    private String label;
    private Long timestamp;
    private String info;
    private String name;
    private String user_name;
    private String user_photo;
    private boolean isPost;
    private String losser;
    private String winner;

    public String getLosser() {
        return losser;
    }

    public void setLosser(String losser) {
        this.losser = losser;
    }

    public boolean getIsPost() {
        return isPost;
    }

    public void setIsPost(boolean post) {
        isPost = post;
    }

    public PointsInfo() {
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

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_photo() {
        return user_photo;
    }

    public void setUser_photo(String user_photo) {
        this.user_photo = user_photo;
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }


}
