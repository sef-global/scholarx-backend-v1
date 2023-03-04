package org.sefglobal.scholarx.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import org.sefglobal.scholarx.util.MentorCategory;
import org.sefglobal.scholarx.util.Views;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "mentor")
@JsonIgnoreProperties({"createdAt", "updatedAt", "assignedMentees", "appliedMentees", "rejectedMentees"})
public class Mentor extends EnrolledUser {

    public Mentor() {
    }

    @JsonView(Views.Public.class)
    @Column
    private String name;

    @JsonView(Views.Public.class)
    @Column
    private String country;

    @JsonView(Views.Public.class)
    @Column
    private String link;

    @JsonView(Views.Public.class)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MentorCategory category;

    @JsonView(Views.Public.class)
    @Column
    private String expertise;

    @JsonView(Views.Public.class)
    @Column
    private String institution;

    @JsonView(Views.Public.class)
    @Column
    private String position;

    @JsonView(Views.Public.class)
    @Column(columnDefinition = "TEXT")
    private String bio;

    @JsonView(Views.Public.class)
    @Column(columnDefinition = "TEXT")
    private String expectations;

    @Column(columnDefinition = "TEXT")
    private String philosophy;

    @Column
    private boolean isCommitted;

    @Column
    private boolean isPastMentor;

    @Column
    private String year;

    @Column(columnDefinition = "TEXT")
    private String motivation;

    @Column(columnDefinition = "TEXT")
    private String changedMotivation;

    @Column(columnDefinition = "TEXT")
    private String reasonForApplying;

    @Column
    private String cvUrl;

    @Column
    private String referee1Name;

    @Column
    private String referee1Email;

    @Column
    private String referee2Name;

    @Column
    private String referee2Email;

    @JsonView(Views.Public.class)
    @Column
    private int slots;

    @JsonView(Views.Public.class)
    @Column
    private int noOfAssignedMentees;

    @OneToMany(mappedBy = "assignedMentor")
    private List<Mentee> assignedMentees = new ArrayList<>();

    @OneToMany(mappedBy = "appliedMentor")
    private List<Mentee> appliedMentees = new ArrayList<>();

    @OneToMany(mappedBy = "rejectedBy")
    private List<Mentee> rejectedMentees = new ArrayList<>();

    public MentorCategory getCategory() {
        return category;
    }

    public void setCategory(MentorCategory category) {
        this.category = category;
    }

    public String getExpertise() {
        return expertise;
    }

    public void setExpertise(String expertise) {
        this.expertise = expertise;
    }

    public String getInstitution() {
        return institution;
    }

    public void setInstitution(String institution) {
        this.institution = institution;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public int getSlots() {
        return slots;
    }

    public void setSlots(int slots) {
        this.slots = slots;
    }

    public List<Mentee> getAssignedMentees() {
        return assignedMentees;
    }

    public void setAssignedMentees(List<Mentee> assignedMentees) {
        this.assignedMentees = assignedMentees;
    }

    public List<Mentee> getAppliedMentees() {
        return appliedMentees;
    }

    public void setAppliedMentees(List<Mentee> appliedMentees) {
        this.appliedMentees = appliedMentees;
    }

    public int getNoOfAssignedMentees() {
        return noOfAssignedMentees;
    }

    public void setNoOfAssignedMentees(int noOfAssignedMentees) {
        this.noOfAssignedMentees = noOfAssignedMentees;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getExpectations() {
        return expectations;
    }

    public void setExpectations(String expectations) {
        this.expectations = expectations;
    }

    public String getPhilosophy() {
        return philosophy;
    }

    public void setPhilosophy(String philosophy) {
        this.philosophy = philosophy;
    }

    public boolean getIsCommitted() {
        return isCommitted;
    }

    public void setIsCommitted(boolean committed) {
        isCommitted = committed;
    }

    public boolean getIsPastMentor() {
        return isPastMentor;
    }

    public void setIsPastMentor(boolean pastMentor) {
        isPastMentor = pastMentor;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getMotivation() {
        return motivation;
    }

    public void setMotivation(String motivation) {
        this.motivation = motivation;
    }

    public String getChangedMotivation() {
        return changedMotivation;
    }

    public void setChangedMotivation(String changedMotivation) {
        this.changedMotivation = changedMotivation;
    }

    public String getReasonForApplying() {
        return reasonForApplying;
    }

    public void setReasonForApplying(String reasonForApplying) {
        this.reasonForApplying = reasonForApplying;
    }

    public String getCvUrl() {
        return cvUrl;
    }

    public void setCvUrl(String cvUrl) {
        this.cvUrl = cvUrl;
    }

    public String getReferee1Name() {
        return referee1Name;
    }

    public void setReferee1Name(String referee1Name) {
        this.referee1Name = referee1Name;
    }

    public String getReferee1Email() {
        return referee1Email;
    }

    public void setReferee1Email(String referee1Email) {
        this.referee1Email = referee1Email;
    }

    public String getReferee2Name() {
        return referee2Name;
    }

    public void setReferee2Name(String referee2Name) {
        this.referee2Name = referee2Name;
    }

    public String getReferee2Email() {
        return referee2Email;
    }

    public void setReferee2Email(String referee2Email) {
        this.referee2Email = referee2Email;
    }
}
