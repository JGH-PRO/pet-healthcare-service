package com.petcare.petcareapp.dto.vaccination;

import java.time.LocalDate;

public class VaccinationDto {
    private Long id;
    private String vaccineName;
    private LocalDate vaccinationDate;
    private String notes;
    private Long petId;

    // Constructor, Getters, and Setters
    public VaccinationDto() {}

    public VaccinationDto(Long id, String vaccineName, LocalDate vaccinationDate, String notes, Long petId) {
        this.id = id;
        this.vaccineName = vaccineName;
        this.vaccinationDate = vaccinationDate;
        this.notes = notes;
        this.petId = petId;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getVaccineName() { return vaccineName; }
    public void setVaccineName(String vaccineName) { this.vaccineName = vaccineName; }
    public LocalDate getVaccinationDate() { return vaccinationDate; }
    public void setVaccinationDate(LocalDate vaccinationDate) { this.vaccinationDate = vaccinationDate; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public Long getPetId() { return petId; }
    public void setPetId(Long petId) { this.petId = petId; }
}
