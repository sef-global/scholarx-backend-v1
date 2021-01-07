package org.sefglobal.scholarx.util;

public enum ProgramState {
    CREATED,
    MENTOR_APPLICATION,
    MENTOR_SELECTION,
    MENTEE_APPLICATION,
    MENTEE_SELECTION,
    MENTOR_CONFIRMATION,
    ONGOING,
    COMPLETED,
    REMOVED;

    private static ProgramState[] states = values();

    public ProgramState next() {
        return states[(this.ordinal() + 1) % states.length];
    }

    public boolean isHigherThan(ProgramState state){
        return this.ordinal()>state.ordinal();
    }
}
