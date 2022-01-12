package org.sefglobal.scholarx.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.ManyToOne;

@Entity
@Table(name = "comment")
public class Comment extends BaseScholarxModel {
    @ManyToOne
    private Mentee mentee;

    @Column
    private String comment;

    @ManyToOne
    private Profile commented_by;

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Profile getCommented_by() {
        return commented_by;
    }

    public void setCommented_by(Profile commented_by) {
        this.commented_by = commented_by;
    }

    public Mentee getMentee() {
        return mentee;
    }

    public void setMentee(Mentee mentee) {
        this.mentee = mentee;
    }
}
