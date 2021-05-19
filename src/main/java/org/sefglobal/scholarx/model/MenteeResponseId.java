package org.sefglobal.scholarx.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class MenteeResponseId implements Serializable {
    @Column(name = "question_id")
    private long questionId;
    @Column(name = "mentee_id")
    private long menteeId;

    public MenteeResponseId() {}

    public MenteeResponseId(long questionId, long menteeId) {
        this.questionId = questionId;
        this.menteeId = menteeId;
    }

    public long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(long questionId) {
        this.questionId = questionId;
    }

    public long getMenteeId() {
        return menteeId;
    }

    public void setMenteeId(long menteeId) {
        this.menteeId = menteeId;
    }
}
