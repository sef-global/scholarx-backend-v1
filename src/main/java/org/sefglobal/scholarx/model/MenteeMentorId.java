package org.sefglobal.scholarx.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class MenteeMentorId implements Serializable {
    @Column(name = "mentee_id")
    private long menteeId;
    @Column(name = "mentor_id")
    private long mentorId;

    public MenteeMentorId() {}

    public MenteeMentorId(long menteeId, long mentorId) {
        this.menteeId = menteeId;
        this.mentorId = mentorId;
    }

    public long getMenteeId() {
        return menteeId;
    }

    public void setMenteeId(long menteeId) {
        this.menteeId = menteeId;
    }

    public long getMentorId() {
        return mentorId;
    }

    public void setMentorId(long mentorId) {
        this.mentorId = mentorId;
    }
}
