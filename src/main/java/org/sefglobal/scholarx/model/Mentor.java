package org.sefglobal.scholarx.model;

public class Mentor extends EnrolledUser{
    private String questionnaire;

    public String getQuestionnaire() {
        return questionnaire;
    }

    public void setQuestionnaire(String questionnaire) {
        this.questionnaire = questionnaire;
    }
}
