package com.petcare.petcareapp.service;

import com.petcare.petcareapp.domain.*;
import com.petcare.petcareapp.domain.model.AppointmentStatus;
import com.petcare.petcareapp.dto.appointment.AppointmentDto;
import com.petcare.petcareapp.dto.appointment.CreateAppointmentRequestDto;
import com.petcare.petcareapp.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AppointmentService {

    @Autowired private AppointmentRepository appointmentRepository;
    @Autowired private PetRepository petRepository;
    @Autowired private ClinicRepository clinicRepository;
    @Autowired private UserRepository userRepository;

    private User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    @Transactional
    public AppointmentDto createAppointment(CreateAppointmentRequestDto requestDto, String username) {
        User user = getUserByUsername(username);
        Pet pet = petRepository.findById(requestDto.getPetId())
                .orElseThrow(() -> new RuntimeException("Pet not found with id: " + requestDto.getPetId()));

        if (pet.getOwner() == null || !pet.getOwner().equals(user)) { // Added null check for pet.getOwner()
            throw new AccessDeniedException("User does not own this pet and cannot create an appointment for it.");
        }

        Clinic clinic = clinicRepository.findById(requestDto.getClinicId())
                .orElseThrow(() -> new RuntimeException("Clinic not found with id: " + requestDto.getClinicId()));

        Appointment appointment = new Appointment();
        appointment.setPet(pet);
        appointment.setClinic(clinic);
        appointment.setAppointmentDateTime(requestDto.getAppointmentDateTime());
        appointment.setReasonForVisit(requestDto.getReasonForVisit());
        appointment.setStatus(AppointmentStatus.PENDING); // Default status

        Appointment savedAppointment = appointmentRepository.save(appointment);
        return mapToDto(savedAppointment);
    }

    @Transactional(readOnly = true)
    public List<AppointmentDto> getAppointmentsForUser(String username) {
        // User user = getUserByUsername(username); // Not strictly needed if using query by username
        return appointmentRepository.findByPet_Owner_UsernameOrderByAppointmentDateTimeDesc(username)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AppointmentDto> getAppointmentsForPet(Long petId, String username) {
        User user = getUserByUsername(username);
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new RuntimeException("Pet not found with id: " + petId));
        if(pet.getOwner() == null || !pet.getOwner().equals(user)){ // Added null check for pet.getOwner()
            throw new AccessDeniedException("User does not own this pet.");
        }
        return appointmentRepository.findByPetIdOrderByAppointmentDateTimeDesc(petId)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }


    private AppointmentDto mapToDto(Appointment appointment) {
        return new AppointmentDto(
                appointment.getId(),
                appointment.getPet().getId(),
                appointment.getPet().getName(),
                appointment.getClinic().getId(),
                appointment.getClinic().getName(),
                appointment.getAppointmentDateTime(),
                appointment.getReasonForVisit(),
                appointment.getStatus()
        );
    }
}
