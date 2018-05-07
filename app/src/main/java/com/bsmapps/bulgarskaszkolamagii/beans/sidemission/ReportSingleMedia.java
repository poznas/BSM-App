package com.bsmapps.bulgarskaszkolamagii.beans.sidemission;

/**
 * Created by Mlody Danon on 7/28/2017.
 */

public class ReportSingleMedia {

    private String type;
    private String orginalUrl;
    private String thumbnailUrl;

    public ReportSingleMedia(String type, String orginalUrl, String thumbnailUrl) {
        this.type = type;
        this.orginalUrl = orginalUrl;
        this.thumbnailUrl = thumbnailUrl;
    }

    public ReportSingleMedia() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOrginalUrl() {
        return orginalUrl;
    }

    public void setOrginalUrl(String orginalUrl) {
        this.orginalUrl = orginalUrl;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }
}
