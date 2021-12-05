package org.sefglobal.scholarx.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "mentor")
@JsonIgnoreProperties({"createdAt", "updatedAt", "mentees"})
public class Mentor extends EnrolledUser {

    public Mentor() {
    }

    @OneToMany(mappedBy = "assignedMentor")
    private List<Mentee> assignedMentees = new ArrayList<>();

    @OneToMany(mappedBy = "appliedMentor")
    private List<Mentee> appliedMentees = new ArrayList<>();

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
}
