package org.sefglobal.scholarx.model;

import org.sefglobal.scholarx.util.ProfileType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "profile")
public class Profile extends BaseScholarxModel {

    @Column(length = 36,
            nullable = false)
    private String uid;

    @Column(nullable = false)
    private String email;

    @Column
    private String firstName;

    @Column
    private String lastName;

    @Column
    private String imageUrl;

    @Column(nullable = false)
    private String linkedinUrl;

    @Column(length = 50)
    private String headline;

    @Enumerated(EnumType.STRING)
    @Column(length = 10,
            nullable = false)
    private ProfileType type;

    @OneToMany(mappedBy = "profile")
    private List<EnrolledUser> enrolledUsers = new ArrayList<>();

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getLinkedinUrl() {
        return linkedinUrl;
    }

    public void setLinkedinUrl(String linkedinUrl) {
        this.linkedinUrl = linkedinUrl;
    }

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public ProfileType getType() {
        return type;
    }

    public void setType(ProfileType type) {
        this.type = type;
    }

    public List<EnrolledUser> getEnrolledUsers() {
        return enrolledUsers;
    }

    public void setEnrolledUsers(List<EnrolledUser> enrolledUsers) {
        this.enrolledUsers = enrolledUsers;
    }
}
