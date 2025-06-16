package com.petcare.petcareapp.dto.vaccination;

import java.time.LocalDate;

public class CreateVaccinationRequestDto {
    private String vaccineName;
    private LocalDate vaccinationDate;
    private String notes;

    // Getters and Setters
    public String getVaccineName() { return vaccineName; }
    public void setVaccineName(String vaccineName) { this.vaccineName = vaccineName; }
    public LocalDate getVaccinationDate() { return vaccinationDate; }
    public void setVaccinationDate(LocalDate vaccinationDate) { this.vaccinationDate = vaccinationDate; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
