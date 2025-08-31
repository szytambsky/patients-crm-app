package com.pmpatient.patientservice.infrastracture.exceptions.patient;

import java.util.UUID;

public class PatientNotFoundException extends RuntimeException {

    private final UUID patientId;

    public PatientNotFoundException(String message, UUID patientId) {
        super(message);
        this.patientId = patientId;
    }

    public UUID getPatientId() {
        return patientId;
    }
}
