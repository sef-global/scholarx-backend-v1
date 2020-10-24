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

    private static ProgramStatus[] statusList = values();

    public ProgramStatus next() {
        return statusList[(this.ordinal() + 1) % statusList.length];
    }
}
