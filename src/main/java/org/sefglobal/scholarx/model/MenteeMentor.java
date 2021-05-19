package org.sefglobal.scholarx.model;

import org.sefglobal.scholarx.util.EnrolmentState;

import javax.persistence.*;

@Entity
@Table(name = "mentee_mentor")
public class MenteeMentor {
    @EmbeddedId
    private MenteeMentorId id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "mentor_id")
    @MapsId("mentorId")
    private Mentor mentor;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "mentee_id")
    @MapsId("menteeId")
    private Mentee mentee;

    @Column
    private EnrolmentState state;

    public MenteeMentor() {}

    public MenteeMentor(Mentee mentee, Mentor mentor, EnrolmentState state) {
        this.id = new MenteeMentorId(mentee.getId(), mentor.getId());
        this.mentee = mentee;
        this.mentor = mentor;
        this.state = state;
    }

    public MenteeMentorId getId() {
        return id;
    }

    public void setId(MenteeMentorId id) {
        this.id = id;
    }

    public Mentor getMentor() {
        return mentor;
    }

    public void setMentor(Mentor mentor) {
        this.mentor = mentor;
    }

    public Mentee getMentee() {
        return mentee;
    }

    public void setMentee(Mentee mentee) {
        this.mentee = mentee;
    }

    public EnrolmentState getState() {
        return state;
    }

    public void setState(EnrolmentState state) {
        this.state = state;
    }
}
