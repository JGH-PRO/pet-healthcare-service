package com.petcare.petcareapp.domain;

import com.petcare.petcareapp.domain.model.PetGender;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "pets")
public class Pet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 100)
    private String breed;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private PetGender gender;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    private Double weight; // in kg or lbs, consistency is key

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @OneToMany(mappedBy = "pet", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Vaccination> vaccinations = new ArrayList<>();

    @OneToMany(mappedBy = "pet", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<HealthLog> healthLogs = new ArrayList<>();

    @OneToMany(mappedBy = "pet", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Appointment> appointments = new ArrayList<>();

    // Constructors
    public Pet() {
    }

    public Pet(String name, String breed, PetGender gender, LocalDate birthDate, Double weight, User owner) {
        this.name = name;
        this.breed = breed;
        this.gender = gender;
        this.birthDate = birthDate;
        this.weight = weight;
        this.owner = owner;
    }

    // Getters and Setters
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
    public User getOwner() { return owner; }
    public void setOwner(User owner) { this.owner = owner; }

    // Getters and Setters for collections
    public List<Vaccination> getVaccinations() { return vaccinations; }
    public void setVaccinations(List<Vaccination> vaccinations) { this.vaccinations = vaccinations; }
    public void addVaccination(Vaccination vaccination) { this.vaccinations.add(vaccination); vaccination.setPet(this); }
    public void removeVaccination(Vaccination vaccination) { this.vaccinations.remove(vaccination); vaccination.setPet(null); }

    public List<HealthLog> getHealthLogs() { return healthLogs; }
    public void setHealthLogs(List<HealthLog> healthLogs) { this.healthLogs = healthLogs; }
    public void addHealthLog(HealthLog healthLog) { this.healthLogs.add(healthLog); healthLog.setPet(this); }
    public void removeHealthLog(HealthLog healthLog) { this.healthLogs.remove(healthLog); healthLog.setPet(null); }

    public List<Appointment> getAppointments() { return appointments; }
    public void setAppointments(List<Appointment> appointments) { this.appointments = appointments; }
    public void addAppointment(Appointment appointment) { this.appointments.add(appointment); appointment.setPet(this); }
    public void removeAppointment(Appointment appointment) { this.appointments.remove(appointment); appointment.setPet(null); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pet pet = (Pet) o;
        return Objects.equals(id, pet.id) &&
               Objects.equals(name, pet.name) &&
               Objects.equals(owner, pet.owner);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, owner);
    }

    @Override
    public String toString() {
        return "Pet{" +
               "id=" + id +
               ", name='" + name + '\'' +
               ", breed='" + breed + '\'' +
               ", gender=" + gender +
               ", birthDate=" + birthDate +
               ", ownerId=" + (owner != null ? owner.getId() : null) +
               '}';
    }
}
