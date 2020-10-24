package org.sefglobal.scholarx.util;

public enum ProgramStatus {
    CREATED,
    MENTOR_APPLICATION,
    MENTOR_SELECTION,
    MENTEE_APPLICATION,
    MENTEE_SELECTION,
    ONGOING,
    COMPLETED,
    REMOVED;

    private static ProgramStatus[] states = values();

    public ProgramStatus next() {
        return states[(this.ordinal() + 1) % states.length];
    }
}
