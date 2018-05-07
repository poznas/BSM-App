package com.bsmapps.bulgarskaszkolamagii.beans.sidemission;

/**
 * Created by Mlody Danon on 7/28/2017.
 */

public class ReportBasicFirebase {

    private String sm_name;
    private String performing_user;
    private String recording_user;
    private Long timestamp;
    private boolean valid;
    private boolean rated;

    public ReportBasicFirebase() {
    }

    public String getSm_name() {
        return sm_name;
    }

    public void setSm_name(String sm_name) {
        this.sm_name = sm_name;
    }

    public String getPerforming_user() {
        return performing_user;
    }

    public void setPerforming_user(String performing_user) {
        this.performing_user = performing_user;
    }

    public String getRecording_user() {
        return recording_user;
    }

    public void setRecording_user(String recording_user) {
        this.recording_user = recording_user;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public boolean isRated() {
        return rated;
    }

    public void setRated(boolean rated) {
        this.rated = rated;
    }
}
