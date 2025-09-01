package com.pmpatient.patientservice.domain;

import com.pmpatient.patientservice.domain.mapper.PatientMapper;
import com.pmpatient.patientservice.infrastracture.exceptions.patient.PatientNotFoundException;
import com.pmpatient.patientservice.infrastracture.exceptions.patient.EmailAlreadyExistsException;
import com.pmpatient.patientservice.infrastracture.input.PatientRequestDto;
import com.pmpatient.patientservice.infrastracture.output.PatientResponseDto;
import com.pmpatient.patientservice.domain.model.Patient;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class PatientService {
    private final PatientRepository patientRepository;

    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    public List<PatientResponseDto> getPatients() {
        List<Patient> patients = patientRepository.findAll();
        return patients.stream()
                .map(PatientMapper::toResponseDto)
                .toList();
    }

    public PatientResponseDto createPatient(PatientRequestDto patientRequestDto) {
        doesEmailAlreadyExists(patientRequestDto);
        Patient newPatient = patientRepository.save(PatientMapper.toModel(patientRequestDto));
        return PatientMapper.toResponseDto(newPatient);
    }

    public PatientResponseDto updatePatient(UUID patientId, PatientRequestDto patientRequestDto) {
        Patient patient = patientRepository.findById(patientId).orElseThrow(() ->
                new PatientNotFoundException("Patient not found with id: ", patientId));
        if (patientRepository.existsByEmailAndIdNot(patientRequestDto.getEmail(), patientId)) {
            throw new EmailAlreadyExistsException("A patient with this email already exists: "
                    + patientRequestDto.getEmail());
        }
        patient.setName(patientRequestDto.getName());
        patient.setAddress(patientRequestDto.getAddress());
        patient.setEmail(patientRequestDto.getEmail());
        patient.setDateOfBirth(LocalDate.parse(patientRequestDto.getDateOfBirth()));
        Patient updatedPatient = patientRepository.save(patient);
        return PatientMapper.toResponseDto(updatedPatient);
    }

    private void doesEmailAlreadyExists(PatientRequestDto patientRequestDto) {
        if (patientRepository.existsByEmail(patientRequestDto.getEmail())) {
            throw new EmailAlreadyExistsException("A patient with this email already exists: "
                    + patientRequestDto.getEmail());
        }
    }
}
