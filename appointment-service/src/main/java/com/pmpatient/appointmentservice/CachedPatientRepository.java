package com.pmpatient.appointmentservice;

import com.pmpatient.appointmentservice.domain.entity.CachedPatient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CachedPatientRepository extends JpaRepository<CachedPatient, UUID> {
}
