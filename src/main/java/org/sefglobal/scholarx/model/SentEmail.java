package org.sefglobal.scholarx.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.ManyToOne;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.sefglobal.scholarx.util.ProgramState;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
 
@Entity
@Table(name = "email")
@JsonIgnoreProperties({"createdAt"})
public class SentEmail extends BaseScholarxModel {
    @Column
    private String email;

    @Column(columnDefinition = "TEXT")
    private String message;

    @ManyToOne
    private Program program;

    @ManyToOne
    private Profile receiver;

    @Enumerated(EnumType.STRING)
    @Column
    private ProgramState state;
    
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMessage() {
        return message;
    }
  
    public void setMessage(String message) {
        this.message = message;
    } 

    public ProgramState getstate() {
        return state;
    }
    public void setState(ProgramState state) {
        this.state = state;
    }
 
    public Program getProgramId() {
        return program;
    }
   
    public void setProgramId(Program program) {
        this.program = program;
    }

    public void setReceiver(Profile receiver) {
        this.receiver = receiver;
    }
    
    public Profile getReceiver() {
        return receiver;
    }

}






