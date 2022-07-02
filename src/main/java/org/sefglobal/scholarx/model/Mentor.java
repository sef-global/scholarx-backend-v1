package org.sefglobal.scholarx.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.sefglobal.scholarx.util.MentorCategory;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "mentor")
@JsonIgnoreProperties({"createdAt", "updatedAt", "assignedMentees", "appliedMentees", "rejectedMentees"})
public class Mentor extends EnrolledUser {

    public Mentor() {
    }

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MentorCategory category;

    @Column
    private String expertise;

    @Column
    private String institution;

    @Column
    private String position;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column
    private int slots;

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
}
