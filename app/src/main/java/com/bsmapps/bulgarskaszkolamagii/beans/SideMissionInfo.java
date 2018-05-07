package com.bsmapps.bulgarskaszkolamagii.beans;

/**
 * Created by Mlody Danon on 7/25/2017.
 */

public class SideMissionInfo {

    private String name;
    private String link;
    private boolean Post;

    public SideMissionInfo() {
    }

    public boolean getPost() {
        return Post;
    }

    public void setPost(boolean post) {
        Post = post;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
