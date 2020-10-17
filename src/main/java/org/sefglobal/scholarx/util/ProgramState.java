package org.sefglobal.scholarx.util;

public enum ProgramState {
    CREATED,
    MENTOR_APPLICATION,
    MENTOR_SELECTION,
    MENTEE_APPLY,
    MENTEE_SELECTION,
    ONGOING,
    COMPLETED,
    REMOVED;

    private static ProgramState[] states = values();

    public ProgramState next() {
        return states[(this.ordinal() + 1) % states.length];
    }
}
