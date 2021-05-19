package org.sefglobal.scholarx.model;

import javax.persistence.*;

@Entity
@Table(name = "mentee_response")
public class MenteeResponse {
    @EmbeddedId
    private MenteeResponseId id;

    @ManyToOne(cascade = CascadeType.ALL)
    @MapsId("questionId")
    @JoinColumn(name = "question_id")
    private Question question;

    @ManyToOne(cascade = CascadeType.ALL)
    @MapsId("menteeId")
    @JoinColumn(name = "mentee_id")
    private Mentee mentee;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String response;

    public MenteeResponse() {}

    public MenteeResponse(Question question, Mentee mentee, String response) {
        this.id = new MenteeResponseId(question.getId(), mentee.getId());
        this.question = question;
        this.mentee = mentee;
        this.response = response;
    }

    public MenteeResponseId getId() {
        return id;
    }

    public void setId(MenteeResponseId id) {
        this.id = id;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public Mentee getMentee() {
        return mentee;
    }

    public void setMentee(Mentee mentee) {
        this.mentee = mentee;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}
