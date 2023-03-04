package org.sefglobal.scholarx.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import org.sefglobal.scholarx.util.Views;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "mentee")
@JsonView(Views.Public.class)
@JsonIgnoreProperties({"createdAt", "updatedAt"})
public class Mentee extends EnrolledUser {

    public Mentee() {
    }

    @Column
    private String university;

    @Column
    private String course;

    @Column
    private String year;

    @Column(columnDefinition = "TEXT")
    private String intent;

    @Column(columnDefinition = "TEXT")
    private String reasonForChoice;

    @Column(columnDefinition = "TEXT")
    private String resumeUrl;

    @Column(columnDefinition = "TEXT")
    private String achievements;

    @ManyToOne(optional = false)
    private Mentor appliedMentor;

    @ManyToOne
    private Mentor assignedMentor;

    @ManyToOne
    private Mentor rejectedBy;

    public Mentor getAppliedMentor() {
        return appliedMentor;
    }

    public void setAppliedMentor(Mentor appliedMentor) {
        this.appliedMentor = appliedMentor;
    }

    public Mentor getAssignedMentor() {
        return assignedMentor;
    }

    public void setAssignedMentor(Mentor assignedMentor) {
        this.assignedMentor = assignedMentor;
    }

    public String getUniversity() {
        return university;
    }

    public void setUniversity(String university) {
        this.university = university;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getIntent() {
        return intent;
    }

    public void setIntent(String intent) {
        this.intent = intent;
    }

    public String getReasonForChoice() {
        return reasonForChoice;
    }

    public void setReasonForChoice(String reasonForChoice) {
        this.reasonForChoice = reasonForChoice;
    }


    public Mentor getRejectedBy() {
        return rejectedBy;
    }

    public void setRejectedBy(Mentor rejectedBy) {
        this.rejectedBy = rejectedBy;
    }

    public String getResumeUrl() {
        return resumeUrl;
    }

    public void setResumeUrl(String resumeUrl) {
        this.resumeUrl = resumeUrl;
    }

    public String getAchievements() {
        return achievements;
    }

    public void setAchievements(String achievements) {
        this.achievements = achievements;
    }
}
