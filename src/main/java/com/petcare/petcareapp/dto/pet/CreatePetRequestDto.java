package com.petcare.petcareapp.dto.pet;

import com.petcare.petcareapp.domain.model.PetGender;
import java.time.LocalDate;

public class CreatePetRequestDto {
    private String name;
    private String breed;
    private PetGender gender;
    private LocalDate birthDate;
    private Double weight;

    // Getters and Setters
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
}
