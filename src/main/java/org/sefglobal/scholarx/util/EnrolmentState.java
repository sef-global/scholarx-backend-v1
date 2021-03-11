package org.sefglobal.scholarx.util;

public enum EnrolmentState {
    PENDING,
    APPROVED,
    REJECTED,
    REMOVED;

    public boolean isHigherThanOrEqual(EnrolmentState state){
        return this.ordinal()>=state.ordinal();
    }
}


