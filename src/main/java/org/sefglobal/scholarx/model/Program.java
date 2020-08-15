package org.sefglobal.scholarx.model;

import org.sefglobal.scholarx.util.ProgramState;

public class Program extends BaseScholarxModel{
    private String title;
    private String headline;
    private String imageUrl;
    private String landingPageUrl;
    private ProgramState state;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getLandingPageUrl() {
        return landingPageUrl;
    }

    public void setLandingPageUrl(String landingPageUrl) {
        this.landingPageUrl = landingPageUrl;
    }

    public ProgramState getState() {
        return state;
    }

    public void setState(ProgramState state) {
        this.state = state;
    }
}
