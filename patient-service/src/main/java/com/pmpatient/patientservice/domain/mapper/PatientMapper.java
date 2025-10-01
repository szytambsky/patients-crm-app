package com.pmpatient.patientservice.domain.mapper;

import com.pmpatient.patientservice.infrastracture.input.PatientRequestDto;
import com.pmpatient.patientservice.infrastracture.output.PagedPatientResponseDto;
import com.pmpatient.patientservice.infrastracture.output.PatientResponseDto;
import com.pmpatient.patientservice.domain.model.Patient;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class PatientMapper {
    private static final String DIGITAL_DOT_DATE_FORMAT = "dd.MM.yyyy";

    public static PatientResponseDto toResponseDto(Patient patient) {
        String uuid = patient.getId().toString();
        String name = patient.getName();
        String address = patient.getAddress();
        String email = patient.getEmail();
        String digitalDate = retrieveDigitalDateRecordingWithSeparate();
        return new PatientResponseDto(uuid, name, email, address, digitalDate);
    }

    public static Patient toModel(PatientRequestDto patientRequestDto) {
        Patient patient = new Patient();
        patient.setName(patientRequestDto.getName());
        patient.setEmail(patientRequestDto.getEmail());
        patient.setAddress(patientRequestDto.getAddress());
        patient.setDateOfBirth(LocalDate.parse(patientRequestDto.getDateOfBirth(), DateTimeFormatter.ISO_DATE));
        patient.setRegisteredDate(LocalDate.parse(patientRequestDto.getRegisteredDate()));
        return patient;
    }

    public static PagedPatientResponseDto toPagedResponseDto(List<PatientResponseDto> patientsResponseDto, Page<Patient> patientPage) {
        PagedPatientResponseDto pagedPatientResponseDto = new PagedPatientResponseDto();
        pagedPatientResponseDto.setPatients(patientsResponseDto);
        pagedPatientResponseDto.setPage(patientPage.getNumber() + 1);
        pagedPatientResponseDto.setSize(patientPage.getSize());
        pagedPatientResponseDto.setTotalPages(patientPage.getTotalPages());
        pagedPatientResponseDto.setTotalElements((int) patientPage.getTotalElements());
        return pagedPatientResponseDto;
    }

    private static String retrieveDigitalDateRecordingWithSeparate() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern(DIGITAL_DOT_DATE_FORMAT));
    }

}
