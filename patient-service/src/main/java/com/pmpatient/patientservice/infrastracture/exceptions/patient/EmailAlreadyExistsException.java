package com.pmpatient.patientservice.infrastracture.exceptions.patient;

public class EmailAlreadyExistsException extends RuntimeException {
  public EmailAlreadyExistsException(String message) {
    super(message);
  }
}
