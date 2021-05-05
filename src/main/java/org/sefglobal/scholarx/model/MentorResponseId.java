package org.sefglobal.scholarx.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class MentorResponseId implements Serializable {
    @Column(name = "question_id")
    private long questionId;
    @Column(name = "mentor_id")
    private long mentorId;

    public MentorResponseId() {}

    public MentorResponseId(long questionId, long mentorId) {
        this.questionId = questionId;
        this.mentorId = mentorId;
    }

    public void setQuestionId(long questionId) {
        this.questionId = questionId;
    }

    public long getQuestionId() {
        return questionId;
    }

    public void setMentorId(long mentorId) {
        this.mentorId = mentorId;
    }

    public long getMentorId() {
        return mentorId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(questionId, mentorId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        MentorResponseId id = (MentorResponseId) obj;
        return id.mentorId == this.mentorId && id.questionId == this.questionId;
    }
}
