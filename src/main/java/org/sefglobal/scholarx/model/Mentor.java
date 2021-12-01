package org.sefglobal.scholarx.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.sefglobal.scholarx.util.MentorCategory;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "mentor")
@JsonIgnoreProperties({"createdAt", "updatedAt", "mentees"})
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

    @Column
    private String bio;

    @Column
    private int slots;

    @OneToMany(mappedBy = "mentor")
    private List<Mentee> mentees = new ArrayList<>();

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

    public List<Mentee> getMentees() {
        return mentees;
    }

    public void setMentees(List<Mentee> mentees) {
        this.mentees = mentees;
    }
}
