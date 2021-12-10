package org.sefglobal.scholarx.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import org.sefglobal.scholarx.util.EnrolmentState;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class EnrolledUser extends BaseScholarxModel {

  @ManyToOne(optional = false)
  private Profile profile;

  @Enumerated(EnumType.STRING)
  @Column(length = 20, nullable = false)
  private EnrolmentState state;

  @ManyToOne(optional = false)
  private Program program;

  public Profile getProfile() {
    return profile;
  }

  public void setProfile(Profile profile) {
    this.profile = profile;
  }

  public EnrolmentState getState() {
    return state;
  }

  public void setState(EnrolmentState state) {
    this.state = state;
  }

  public Program getProgram() {
    return program;
  }

  public void setProgram(Program program) {
    this.program = program;
  }
}
