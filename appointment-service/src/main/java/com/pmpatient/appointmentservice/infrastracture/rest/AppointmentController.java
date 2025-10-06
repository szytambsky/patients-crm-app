package com.pmpatient.appointmentservice.infrastracture.rest;

import com.pmpatient.appointmentservice.domain.AppointmentService;
import com.pmpatient.appointmentservice.infrastracture.output.AppointmentResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/appointments")
public class AppointmentController {
    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @GetMapping
    public ResponseEntity<List<AppointmentResponseDto>> getAppointmentsByRangeDate(@RequestParam LocalDateTime from,
                                                                                   @RequestParam LocalDateTime to) {
        List<AppointmentResponseDto> appointmentsByDateRange
                = appointmentService.getAppointmentsByDateRange(from, to);
        return ResponseEntity.ok(appointmentsByDateRange);
    }
}
