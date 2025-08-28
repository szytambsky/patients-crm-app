package com.pmpatient.patientservice.domain;

import com.pmpatient.patientservice.domain.mapper.PatientMapper;
import com.pmpatient.patientservice.infrastracture.output.PatientResponseDto;
import com.pmpatient.patientservice.model.Patient;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PatientService {
    private final PatientRepository patientRepository;
    private final PatientMapper patientMapper;

    public PatientService(PatientRepository patientRepository, PatientMapper patientMapper) {
        this.patientRepository = patientRepository;
        this.patientMapper = patientMapper;
    }

    public List<PatientResponseDto> getPatients() {
        List<Patient> patients = patientRepository.findAll();
        return patients.stream()
                .map(patientMapper::toPatientResponseDto)
                .toList();
    }
}
