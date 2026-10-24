package com.petgrooming.appointments.appointment;

import java.util.EnumSet;
import java.util.Set;

public enum AppointmentStatus {
    PENDING,
    CONFIRMED,
    COMPLETED,
    CANCELLED;

    public boolean canTransitionTo(AppointmentStatus next) {
        if (next == null || next == this) {
            return false;
        }

        Set<AppointmentStatus> allowed = switch (this) {
            case PENDING -> EnumSet.of(CONFIRMED, CANCELLED);
            case CONFIRMED -> EnumSet.of(COMPLETED, CANCELLED);
            case COMPLETED, CANCELLED -> EnumSet.noneOf(AppointmentStatus.class);
        };

        return allowed.contains(next);
    }
}
