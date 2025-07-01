package com.petcare.petcareapp.repository;

import com.petcare.petcareapp.domain.Appointment;
import com.petcare.petcareapp.domain.Clinic;
import com.petcare.petcareapp.domain.Pet;
import com.petcare.petcareapp.domain.model.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByPet(Pet pet);
    List<Appointment> findByPetId(Long petId); // Convenience if you only have petId

    List<Appointment> findByClinic(Clinic clinic);
    List<Appointment> findByClinicId(Long clinicId); // Convenience if you only have clinicId

    List<Appointment> findByPetAndAppointmentDateTimeBetween(Pet pet, LocalDateTime startDateTime, LocalDateTime endDateTime);
    List<Appointment> findByClinicAndAppointmentDateTimeBetween(Clinic clinic, LocalDateTime startDateTime, LocalDateTime endDateTime);
    List<Appointment> findByPetAndStatus(Pet pet, AppointmentStatus status);
    List<Appointment> findByClinicAndStatus(Clinic clinic, AppointmentStatus status);

    List<Appointment> findByPet_Owner_UsernameOrderByAppointmentDateTimeDesc(String username);
    List<Appointment> findByPetIdOrderByAppointmentDateTimeDesc(Long petId);
    // Add more specific finders if needed, e.g., by status, date range for a user
}
