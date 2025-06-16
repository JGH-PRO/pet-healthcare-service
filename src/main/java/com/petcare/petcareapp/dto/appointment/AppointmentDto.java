package com.petcare.petcareapp.dto.appointment;

import com.petcare.petcareapp.domain.model.AppointmentStatus;
import java.time.LocalDateTime;

public class AppointmentDto {
    private Long id;
    private Long petId;
    private String petName; // Denormalized for convenience
    private Long clinicId;
    private String clinicName; // Denormalized for convenience
    private LocalDateTime appointmentDateTime;
    private String reasonForVisit;
    private AppointmentStatus status;

    // Constructor
    public AppointmentDto() {}

    public AppointmentDto(Long id, Long petId, String petName, Long clinicId, String clinicName, LocalDateTime appointmentDateTime, String reasonForVisit, AppointmentStatus status) {
        this.id = id;
        this.petId = petId;
        this.petName = petName;
        this.clinicId = clinicId;
        this.clinicName = clinicName;
        this.appointmentDateTime = appointmentDateTime;
        this.reasonForVisit = reasonForVisit;
        this.status = status;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getPetId() { return petId; }
    public void setPetId(Long petId) { this.petId = petId; }
    public String getPetName() { return petName; }
    public void setPetName(String petName) { this.petName = petName; }
    public Long getClinicId() { return clinicId; }
    public void setClinicId(Long clinicId) { this.clinicId = clinicId; }
    public String getClinicName() { return clinicName; }
    public void setClinicName(String clinicName) { this.clinicName = clinicName; }
    public LocalDateTime getAppointmentDateTime() { return appointmentDateTime; }
    public void setAppointmentDateTime(LocalDateTime appointmentDateTime) { this.appointmentDateTime = appointmentDateTime; }
    public String getReasonForVisit() { return reasonForVisit; }
    public void setReasonForVisit(String reasonForVisit) { this.reasonForVisit = reasonForVisit; }
    public AppointmentStatus getStatus() { return status; }
    public void setStatus(AppointmentStatus status) { this.status = status; }
}
