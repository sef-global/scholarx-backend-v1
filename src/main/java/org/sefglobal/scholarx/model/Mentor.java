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

    @OneToMany(mappedBy = "mentor")
    private List<MentorResponse> mentorResponses;

    public Mentor() {
    }

    @OneToMany(mappedBy = "mentor")
    private List<Mentee> mentees = new ArrayList<>();

    public List<Mentee> getMentees() {
        return mentees;
    }

    public void setMentees(List<Mentee> mentees) {
        this.mentees = mentees;
    }
}
