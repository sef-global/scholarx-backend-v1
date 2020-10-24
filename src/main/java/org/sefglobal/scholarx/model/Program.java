package org.sefglobal.scholarx.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.sefglobal.scholarx.util.ProgramStatus;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "program")
@JsonIgnoreProperties({"createdAt", "updatedAt", "enrolledUsers"})
public class Program extends BaseScholarxModel {

    @Column
    private String title;

    @Column
    private String headline;

    @Column
    private String imageUrl;

    @Column
    private String landingPageUrl;

    @JsonIgnore
    @Enumerated(EnumType.STRING)
    @Column(length = 20,
            nullable = false)
    private ProgramStatus status;

    @OneToMany(mappedBy = "program")
    private List<EnrolledUser> enrolledUsers = new ArrayList<>();

    public Program() {
    }

    public Program(String title, String headline, String imageUrl, String landingPageUrl,
                   ProgramStatus status) {
        this.title = title;
        this.headline = headline;
        this.imageUrl = imageUrl;
        this.landingPageUrl = landingPageUrl;
        this.status = status;
    }

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

    @JsonProperty
    public ProgramStatus getState() {
        return status;
    }

    @JsonIgnore
    public void setState(ProgramStatus status) {
        this.status = status;
    }

    public List<EnrolledUser> getEnrolledUsers() {
        return enrolledUsers;
    }

    public void setEnrolledUsers(List<EnrolledUser> enrolledUsers) {
        this.enrolledUsers = enrolledUsers;
    }
}
