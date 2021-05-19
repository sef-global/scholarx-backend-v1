package org.sefglobal.scholarx.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.sefglobal.scholarx.util.EnrolmentState;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "mentor")
@JsonIgnoreProperties({"createdAt", "updatedAt", "mentees"})
public class Mentor extends EnrolledUser {

    @Enumerated(EnumType.STRING)
    @Column(length = 10, nullable = false)
    private EnrolmentState state;

    @OneToMany(mappedBy = "mentor")
    private List<MentorResponse> mentorResponses;

    @OneToMany(mappedBy = "mentor")
    private List<MenteeMentor> mentees;

    public Mentor() {}

    public List<MenteeMentor> getMentees() {
        return mentees;
    }

    public void setMentees(List<MenteeMentor> mentees) {
        this.mentees = mentees;
    }

    public EnrolmentState getState() {
        return this.state;
    }

    public void setState(EnrolmentState state) {
        this.state = state;
    }
}
