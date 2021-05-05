package org.sefglobal.scholarx.model;

import org.sefglobal.scholarx.util.QuestionCategory;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "question")
public class Question extends BaseScholarxModel{

    @Column(nullable = false, columnDefinition = "TEXT")
    private String question;

    @Column(nullable = false)
    private QuestionCategory category;

    @ManyToOne
    @JoinColumn(name = "program_id")
    private Program program;

    @OneToMany(mappedBy = "question")
    private List<MentorResponse> mentorResponses;

    public Question() {}

    public Question(String question, QuestionCategory category, Program program) {
        this.question = question;
        this.category = category;
        this.program = program;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public QuestionCategory getCategory() {
        return category;
    }

    public void setCategory(QuestionCategory category) {
        this.category = category;
    }

    public Program getProgram() {
        return program;
    }

    public void setProgram(Program program) {
        this.program = program;
    }
}
