package com.pmpatient.patientservice.infrastracture.output;

public record PatientResponseDto(String id,
                                 String name,
                                 String email,
                                 String address,
                                 String dateOfBirth) {
}
