package org.sefglobal.scholarx.model;

import org.sefglobal.scholarx.util.ProgramState;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "program")
public class Program extends BaseScholarxModel{

    @Column
    private String title;

    @Column
    private String headline;

    @Column
    private String imageUrl;

    @Column
    private String landingPageUrl;

    @Enumerated(EnumType.STRING)
    @Column(length = 10,
            nullable = false)
    private ProgramState state;

    @OneToMany(mappedBy = "program")
    private List<EnrolledUser> enrolledUsers = new ArrayList<>();

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

    public List<EnrolledUser> getEnrolledUsers() {
        return enrolledUsers;
    }

    public void setEnrolledUsers(List<EnrolledUser> enrolledUsers) {
        this.enrolledUsers = enrolledUsers;
    }
}
