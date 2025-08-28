package com.pmpatient.patientservice.domain.mapper;

import com.pmpatient.patientservice.infrastracture.output.PatientResponseDto;
import com.pmpatient.patientservice.model.Patient;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class PatientMapper {
    private static final String DIGITAL_DOT_DATE_FORMAT = "dd.MM.yyyy";

    public PatientResponseDto toPatientResponseDto(Patient patient) {
        String uuid = patient.getId().toString();
        String name = patient.getName();
        String address = patient.getAddress();
        String email = patient.getEmail();
        String digitalDate = retrieveDigitalDateRecordingWithSeparate();
        return new PatientResponseDto(uuid, name, email, address, digitalDate);
    }

    private static String retrieveDigitalDateRecordingWithSeparate() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern(DIGITAL_DOT_DATE_FORMAT));
    }
}
