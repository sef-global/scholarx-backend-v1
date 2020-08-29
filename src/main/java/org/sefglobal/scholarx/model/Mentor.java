package org.sefglobal.scholarx.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "mentor")
public class Mentor extends EnrolledUser {

    @Column
    private String questionnaire;

    public Mentor() {
    }

    @OneToMany(mappedBy = "mentor")
    private List<Mentee> mentees = new ArrayList<>();

    public String getQuestionnaire() {
        return questionnaire;
    }

    public void setQuestionnaire(String questionnaire) {
        this.questionnaire = questionnaire;
    }

    public List<Mentee> getMentees() {
        return mentees;
    }

    public void setMentees(List<Mentee> mentees) {
        this.mentees = mentees;
    }
}
