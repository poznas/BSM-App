package com.bsmapps.bulgarskaszkolamagii.beans.sidemission;

/**
 * Created by Mlody Danon on 7/31/2017.
 */

public class PendingReport {

    private String sm_name;
    private String performing_user;
    private String user_photoUrl;
    private Long timestamp;
    private String rpid;
    private boolean post;

    public PendingReport() {
    }

    public boolean getPost() {
        return post;
    }

    public void setPost(boolean post) {
        this.post = post;
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

    public String getUser_photoUrl() {
        return user_photoUrl;
    }

    public void setUser_photoUrl(String user_photoUrl) {
        this.user_photoUrl = user_photoUrl;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getRpid() {
        return rpid;
    }

    public void setRpid(String rpid) {
        this.rpid = rpid;
    }
}
