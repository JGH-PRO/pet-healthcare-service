package com.petcare.petcareapp.dto.pet;

import com.petcare.petcareapp.domain.model.PetGender;
import java.time.LocalDate;

public class PetDto {
    private Long id;
    private String name;
    private String breed;
    private PetGender gender;
    private LocalDate birthDate;
    private Double weight;
    private Long ownerId;

    // Constructor, Getters, and Setters
    public PetDto() {}

    public PetDto(Long id, String name, String breed, PetGender gender, LocalDate birthDate, Double weight, Long ownerId) {
        this.id = id;
        this.name = name;
        this.breed = breed;
        this.gender = gender;
        this.birthDate = birthDate;
        this.weight = weight;
        this.ownerId = ownerId;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getBreed() { return breed; }
    public void setBreed(String breed) { this.breed = breed; }
    public PetGender getGender() { return gender; }
    public void setGender(PetGender gender) { this.gender = gender; }
    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }
    public Double getWeight() { return weight; }
    public void setWeight(Double weight) { this.weight = weight; }
    public Long getOwnerId() { return ownerId; }
    public void setOwnerId(Long ownerId) { this.ownerId = ownerId; }
}
