package com.pmpatient.patientservice.infrastracture.aspects;

import io.micrometer.core.instrument.MeterRegistry;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class PatientServiceMetrics {

    private final MeterRegistry meterRegistry;

    public PatientServiceMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    /*
        After @Cachable tag checks redis, but before PatientService.getPatients().
        First step is check redis cache if there is no entry in cache, second step is calling @Aspect behind the scenes
        which will log cache miss and a third step is to continue execution flow as normal calling getPatients() method.
     */
    @Around("execution(* com.pmpatient.patientservice.domain.PatientService.getPatients(..))")
    public Object monitorGetPatients(ProceedingJoinPoint joinPoint) throws Throwable {
        meterRegistry.counter("custom.redis.cache.miss", "cache", "patients")
                .increment();
        Object result = joinPoint.proceed();
        return result;
    }
}
