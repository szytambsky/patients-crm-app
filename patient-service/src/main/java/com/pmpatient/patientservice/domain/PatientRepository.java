package com.pmpatient.patientservice.domain;

import com.pmpatient.patientservice.domain.model.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PatientRepository extends JpaRepository<Patient, UUID> {
    boolean existsByEmailAndIdNot(String email, UUID id);

    boolean existsByEmail(String email);

    Page<Patient> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
