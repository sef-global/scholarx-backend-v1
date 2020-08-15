package org.sefglobal.scholarx.model;

import org.sefglobal.scholarx.util.EnrolmentState;

public abstract class EnrolledUser extends BaseScholarxModel{
    private Profile profile;
    private EnrolmentState state;
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
