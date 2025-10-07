package com.pmpatient.appointmentservice.domain;

import com.pmpatient.appointmentservice.CachedPatientRepository;
import com.pmpatient.appointmentservice.domain.entity.Appointment;
import com.pmpatient.appointmentservice.domain.entity.CachedPatient;
import com.pmpatient.appointmentservice.infrastracture.output.AppointmentResponseDto;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final CachedPatientRepository cachedPatientRepository;

    public AppointmentService(AppointmentRepository appointmentRepository, CachedPatientRepository cachedPatientRepository) {
        this.appointmentRepository = appointmentRepository;
        this.cachedPatientRepository = cachedPatientRepository;
    }

    public List<AppointmentResponseDto> getAppointmentsByDateRange(LocalDateTime from, LocalDateTime to) {
        List<Appointment> appointmentsBetweenRangeDate = appointmentRepository.findByStartTimeBetween(from, to);
        List<AppointmentResponseDto> appointmentBetweenRangeDateResponse = appointmentsBetweenRangeDate
                .stream()
                .map(appointment -> { // todo: add mapstruct
                    String patientName = cachedPatientRepository.findById(appointment.getPatientId())
                            .map(CachedPatient::getFullName)
                            .orElse("Unknown");
                    AppointmentResponseDto appointmentResponseDto = new AppointmentResponseDto();
                    appointmentResponseDto.setPatientName(patientName);
                    appointmentResponseDto.setId(appointment.getId());
                    appointmentResponseDto.setPatientId(appointment.getPatientId());
                    appointmentResponseDto.setStartTime(appointment.getStartTime());
                    appointmentResponseDto.setEndTime(appointment.getEndTime());
                    appointmentResponseDto.setReason(appointment.getReason());
                    appointmentResponseDto.setVersion(appointment.getVersion());
                    return appointmentResponseDto;
                })
                .toList();
        return appointmentBetweenRangeDateResponse;
    }
}
