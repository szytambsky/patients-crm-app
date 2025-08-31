package com.pmpatient.patientservice.infrastracture.output.rest;

import com.pmpatient.patientservice.domain.PatientService;
import com.pmpatient.patientservice.infrastracture.input.PatientRequestDto;
import com.pmpatient.patientservice.infrastracture.input.validators.CreatePatientValidationGroup;
import com.pmpatient.patientservice.infrastracture.output.PatientResponseDto;
import jakarta.validation.groups.Default;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/patients")
public class PatientController {
    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @GetMapping
    public ResponseEntity<List<PatientResponseDto>> getPatients() { // todo: make it pagable
        List<PatientResponseDto> patients = patientService.getPatients();
        return ResponseEntity.ok().body(patients);
    }

    @PostMapping
    public ResponseEntity<PatientResponseDto> createPatient(@Validated({Default.class, CreatePatientValidationGroup.class})
                                                            @RequestBody PatientRequestDto patientRequestDto) {
        PatientResponseDto createdPatient = patientService.createPatient(patientRequestDto);
        return ResponseEntity.ok().body(createdPatient);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PatientResponseDto> updatePatient(@PathVariable UUID id,
                                                            @Validated({Default.class}) @RequestBody PatientRequestDto patientRequestDto) {
        PatientResponseDto patientResponseDto = patientService.updatePatient(id, patientRequestDto);
        return ResponseEntity.ok().body(patientResponseDto);
    }
}
