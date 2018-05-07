package com.bsmapps.bulgarskaszkolamagii.beans;

/**
 * Created by Mlody Danon on 7/20/2017.
 */

public class User {
    private String displayName;
    private String email;
    private String facebook;
    private String gender;
    private String instanceId;
    private String label;
    private String photoUrl;
    private String team;

    public User(){
    }

    public User( String displayName, String email, String photoUrl ){
        this.email = email;
        this.photoUrl = photoUrl;
        this.displayName = displayName;
    }

    public String getDisplayName() {return displayName;}

    public void setDisplayName(String displayName) {this.displayName = displayName;}

    public String getEmail() {return email;}

    public void setEmail(String email) {this.email = email;}

    public String getFacebook() {return facebook;}

    public void setFacebook(String facebook) {this.facebook = facebook;}

    public String getGender() {return gender;}

    public void setGender(String gender) {this.gender = gender;}

    public String getInstanceId() {return instanceId;}

    public void setInstanceId(String instanceId) {this.instanceId = instanceId;}

    public String getLabel() {return label;}

    public void setLabel(String label) {this.label = label;}

    public String getPhotoUrl() {return photoUrl;}

    public void setPhotoUrl(String photoUrl) {this.photoUrl = photoUrl;}

    public String getTeam() {return team;}

    public void setTeam(String team) {this.team = team;}
}
