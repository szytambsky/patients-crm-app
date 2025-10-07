package com.pmpatient.appointmentservice.domain;

import com.pmpatient.appointmentservice.domain.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {
    List<Appointment> findByStartTimeBetween(LocalDateTime from, LocalDateTime to);
}
