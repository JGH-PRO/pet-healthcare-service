package com.petcare.petcareapp.service;

import com.petcare.petcareapp.domain.Clinic;
import com.petcare.petcareapp.dto.clinic.ClinicDto;
// Import CreateClinicRequestDto if admin methods were to be added now
import com.petcare.petcareapp.repository.ClinicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClinicService {

    @Autowired
    private ClinicRepository clinicRepository;

    @Transactional(readOnly = true)
    public List<ClinicDto> getAllClinics() {
        return clinicRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ClinicDto getClinicById(Long clinicId) {
        Clinic clinic = clinicRepository.findById(clinicId)
            .orElseThrow(() -> new RuntimeException("Clinic not found with id: " + clinicId));
        return mapToDto(clinic);
    }

    // Placeholder for future admin functionality to add/update clinics
    // @Transactional
    // public ClinicDto createClinic(CreateClinicRequestDto requestDto) { ... }

    private ClinicDto mapToDto(Clinic clinic) {
        return new ClinicDto(
                clinic.getId(),
                clinic.getName(),
                clinic.getAddress(),
                clinic.getPhoneNumber(),
                clinic.getOperatingHours(),
                clinic.getServicesOffered(),
                clinic.getLatitude(),
                clinic.getLongitude()
        );
    }
}
