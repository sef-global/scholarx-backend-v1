package org.sefglobal.scholarx.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "mentor")
@JsonIgnoreProperties({"createdAt", "updatedAt", "mentees"})
public class Mentor extends EnrolledUser {

    @Column
    private String application;

    @Column
    private String prerequisites;

    public Mentor() {
    }

    @OneToMany(mappedBy = "mentor")
    private List<Mentee> mentees = new ArrayList<>();

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public String getPrerequisites() {
        return prerequisites;
    }

    public void setPrerequisites(String prerequisites) {
        this.prerequisites = prerequisites;
    }

    public List<Mentee> getMentees() {
        return mentees;
    }

    public void setMentees(List<Mentee> mentees) {
        this.mentees = mentees;
    }
}
