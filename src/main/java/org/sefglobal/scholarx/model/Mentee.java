package org.sefglobal.scholarx.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "mentee")
@JsonIgnoreProperties({"createdAt", "updatedAt"})
public class Mentee extends EnrolledUser {

    @OneToMany(mappedBy = "mentee")
    private List<MenteeResponse> menteeResponses;

    @OneToMany(mappedBy = "mentee")
    private List<MenteeMentor> appliedMentors;

    public Mentee() {}
}
