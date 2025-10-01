package com.pmpatient.patientservice.infrastracture.output;

import java.util.List;

public class PagedPatientResponseDto {
    private List<PatientResponseDto> patients;
    private int page;
    private int size;
    private int totalPages;
    private int totalElements;

    public PagedPatientResponseDto() {}

    public PagedPatientResponseDto(List<PatientResponseDto> patients,
                                   int page, int size, int totalPages, int totalElements) {
        this.patients = patients;
        this.page = page;
        this.size = size;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
    }

    public List<PatientResponseDto> getPatients() {
        return patients;
    }

    public void setPatients(List<PatientResponseDto> patients) {
        this.patients = patients;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(int totalElements) {
        this.totalElements = totalElements;
    }
}
