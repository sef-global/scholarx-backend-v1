package org.sefglobal.scholarx.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "mentor_response")
public class MentorResponse implements Serializable {

    @EmbeddedId
    MentorResponseId id;

    @ManyToOne(cascade = CascadeType.ALL)
    @MapsId("questionId")
    @JoinColumn(name = "question_id")
    private Question question;

    @ManyToOne(cascade = CascadeType.ALL)
    @MapsId("mentorId")
    @JoinColumn(name = "mentor_id")
    private Mentor mentor;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String response;

    public MentorResponse() {}

    public MentorResponse(Question question, Mentor mentor, String response) {
        this.id = new MentorResponseId(question.getId(), mentor.getId());
        this.question = question;
        this.mentor = mentor;
        this.response = response;
    }

    public MentorResponseId getId() {
        return id;
    }

    public void setId(MentorResponseId id) {
        this.id = id;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public Mentor getMentor() {
        return mentor;
    }

    public void setMentor(Mentor mentor) {
        this.mentor = mentor;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}
