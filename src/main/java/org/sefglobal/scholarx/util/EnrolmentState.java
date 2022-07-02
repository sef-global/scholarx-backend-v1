package org.sefglobal.scholarx.util;

public enum EnrolmentState {
    PENDING,
    POOL,
    ASSIGNED,
    APPROVED,
    DISCARDED,
    REJECTED,
    FAILED_FROM_WILDCARD,
    REMOVED;

    public boolean isHigherThanOrEqual(EnrolmentState state){
        return this.ordinal()>=state.ordinal();
    }
}


