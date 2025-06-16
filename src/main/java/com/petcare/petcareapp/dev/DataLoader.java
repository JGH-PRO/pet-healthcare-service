package com.petcare.petcareapp.dev;

import com.petcare.petcareapp.domain.*;
import com.petcare.petcareapp.domain.model.*;
import com.petcare.petcareapp.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired private UserRepository userRepository;
    @Autowired private PetRepository petRepository;
    @Autowired private VaccinationRepository vaccinationRepository;
    @Autowired private HealthLogRepository healthLogRepository;
    @Autowired private ClinicRepository clinicRepository; // Assuming this exists
    @Autowired private AppointmentRepository appointmentRepository; // Assuming this exists
    @Autowired private PostRepository postRepository;
    @Autowired private CommentRepository commentRepository;
    @Autowired private PostLikeRepository postLikeRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @Override
    @Transactional // Ensure all operations are part of a single transaction
    public void run(String... args) throws Exception {
        // Check a central repository, e.g., UserRepository, to prevent re-seeding.
        if (userRepository.count() > 0) {
            System.out.println("Data already loaded. Skipping seed data generation.");
            return;
        }
        System.out.println("Loading seed data...");

        // Seed Users
        User user1 = new User("userone", passwordEncoder.encode("password123"), "userone@example.com", "USER");
        User user2 = new User("usertwo", passwordEncoder.encode("password456"), "usertwo@example.com", "USER");
        User adminUser = new User("adminuser", passwordEncoder.encode("adminpass"), "admin@example.com", "ADMIN"); // Example admin
        userRepository.saveAll(Arrays.asList(user1, user2, adminUser));
        System.out.println("Seeded Users: " + userRepository.count());

        // Seed Pets
        Pet pet1 = new Pet("Buddy", "Golden Retriever", PetGender.MALE, LocalDate.of(2020, 1, 15), 30.5, user1);
        Pet pet2 = new Pet("Lucy", "Siamese", PetGender.FEMALE, LocalDate.of(2019, 5, 10), 4.2, user1);
        Pet pet3 = new Pet("Charlie", "Labrador", PetGender.MALE, LocalDate.of(2021, 8, 1), 25.0, user2);
        // Ensure pets are saved before using them in child entities if IDs are needed immediately by other logic
        // For simple seeding like this, saveAll is fine.
        petRepository.saveAll(Arrays.asList(pet1, pet2, pet3));
        System.out.println("Seeded Pets: " + petRepository.count());

        // Seed Vaccinations
        Vaccination vax1_pet1 = new Vaccination(pet1, "Rabies", LocalDate.of(2023, 3, 1), "Annual booster");
        Vaccination vax2_pet1 = new Vaccination(pet1, "Distemper", LocalDate.of(2023, 3, 1), "Part of DAPP");
        Vaccination vax1_pet2 = new Vaccination(pet2, "FVRCP", LocalDate.of(2022, 12, 5), "Annual cat vaccine");
        vaccinationRepository.saveAll(Arrays.asList(vax1_pet1, vax2_pet1, vax1_pet2));
        System.out.println("Seeded Vaccinations: " + vaccinationRepository.count());

        // Seed Health Logs
        HealthLog hl1_pet1 = new HealthLog(pet1, HealthLogType.MEAL, LocalDateTime.now().minusHours(2), "Dry Kibble", 2.0, "cups");
        HealthLog hl2_pet1 = new HealthLog(pet1, HealthLogType.WALK, LocalDateTime.now().minusHours(1), "Morning walk in the park", 30.0, "minutes");
        HealthLog hl3_pet1 = new HealthLog(pet1, HealthLogType.WEIGHT, LocalDateTime.now().minusDays(1), "Routine check", 30.6, "kg");
        HealthLog hl1_pet2 = new HealthLog(pet2, HealthLogType.MEAL, LocalDateTime.now().minusHours(3), "Wet food", 1.0, "can");
        healthLogRepository.saveAll(Arrays.asList(hl1_pet1, hl2_pet1, hl3_pet1, hl1_pet2));
        System.out.println("Seeded Health Logs: " + healthLogRepository.count());

        // Seed Clinics
        Clinic clinic1 = new Clinic();
        clinic1.setName("Happy Paws Vet Clinic");
        clinic1.setAddress("123 Vet Street, Anytown, USA");
        clinic1.setPhoneNumber("555-0101");
        clinic1.setOperatingHours("Mon-Fri 9am-6pm, Sat 9am-1pm");
        clinic1.setServicesOffered("General Checkups, Vaccinations, Dental Care, Emergency Services");
        clinic1.setLatitude(34.0522); clinic1.setLongitude(-118.2437); // Los Angeles approx.

        Clinic clinic2 = new Clinic();
        clinic2.setName("The Healthy Pet Center");
        clinic2.setAddress("456 Animal Ave, Otherville, USA");
        clinic2.setPhoneNumber("555-0202");
        clinic2.setOperatingHours("Mon-Sun 8am-8pm (24/7 Emergency)");
        clinic2.setServicesOffered("Surgery, Advanced Diagnostics, Wellness Exams, Grooming");
        clinic2.setLatitude(34.0560); clinic2.setLongitude(-118.2500);

        Clinic clinic3 = new Clinic();
        clinic3.setName("Critter Care Hospital");
        clinic3.setAddress("789 Creature Rd, Petburg, USA");
        clinic3.setPhoneNumber("555-0303");
        clinic3.setOperatingHours("Mon-Fri 8am-5pm");
        clinic3.setServicesOffered("Exotic Pets, General Medicine, Vaccinations");
        clinic3.setLatitude(34.0500); clinic3.setLongitude(-118.2400);
        clinicRepository.saveAll(Arrays.asList(clinic1, clinic2, clinic3));
        System.out.println("Seeded Clinics: " + clinicRepository.count());

        // Seed Minimal Appointments
        if (pet1 != null && clinic1 != null) {
            Appointment app1 = new Appointment();
            app1.setPet(pet1);
            app1.setClinic(clinic1);
            app1.setAppointmentDateTime(LocalDateTime.now().plusDays(7).withHour(10).withMinute(0));
            app1.setReasonForVisit("Annual Checkup");
            app1.setStatus(AppointmentStatus.CONFIRMED);
            appointmentRepository.save(app1);
        }
        if (pet3 != null && clinic2 != null) {
            Appointment app2 = new Appointment();
            app2.setPet(pet3);
            app2.setClinic(clinic2);
            app2.setAppointmentDateTime(LocalDateTime.now().plusDays(10).withHour(14).withMinute(30));
            app2.setReasonForVisit("Vaccination Booster");
            app2.setStatus(AppointmentStatus.PENDING);
            appointmentRepository.save(app2);
        }
        System.out.println("Seeded Appointments: " + appointmentRepository.count());

        // Seed Posts
        if (user1 != null && user2 != null && adminUser != null && pet1 != null && pet2 != null && pet3 != null) {
            Post post1_user1 = new Post();
            post1_user1.setAuthor(user1);
            post1_user1.setContent("Enjoying a sunny day with " + pet1.getName() + "!");
            post1_user1.setImageUrl("https://example.com/placeholder_pet_image1.jpg");
            postRepository.save(post1_user1);

            Post post2_user2 = new Post();
            post2_user2.setAuthor(user2);
            post2_user2.setContent("My lovely " + pet3.getName() + " is always up for an adventure.");
            post2_user2.setImageUrl("https://example.com/placeholder_pet_image2.jpg");
            postRepository.save(post2_user2);

            Post post3_user1 = new Post();
            post3_user1.setAuthor(user1);
            post3_user1.setContent(pet2.getName() + " napping in a sunbeam. So cute!");
            // No image for this one
            postRepository.save(post3_user1);
            System.out.println("Seeded Posts: " + postRepository.count());

            // Seed Comments
            Comment comment1_post1 = new Comment();
            comment1_post1.setPost(post1_user1);
            comment1_post1.setAuthor(user2);
            comment1_post1.setContent("Aww, looks like a great day!");
            commentRepository.save(comment1_post1);

            Comment comment2_post1 = new Comment();
            comment2_post1.setPost(post1_user1);
            comment2_post1.setAuthor(adminUser);
            comment2_post1.setContent("Beautiful pet!");
            commentRepository.save(comment2_post1);

            Comment comment1_post2 = new Comment();
            comment1_post2.setPost(post2_user2);
            comment1_post2.setAuthor(user1);
            comment1_post2.setContent("So adorable! We should arrange a playdate.");
            commentRepository.save(comment1_post2);
            System.out.println("Seeded Comments: " + commentRepository.count());

            // Seed Likes
            PostLike like1_post1_user2 = new PostLike(post1_user1, user2);
            postLikeRepository.save(like1_post1_user2);

            PostLike like2_post1_admin = new PostLike(post1_user1, adminUser);
            postLikeRepository.save(like2_post1_admin);

            PostLike like1_post2_user1 = new PostLike(post2_user2, user1);
            postLikeRepository.save(like1_post2_user1);

            PostLike like1_post3_user2 = new PostLike(post3_user1, user2);
            postLikeRepository.save(like1_post3_user2);
            System.out.println("Seeded PostLikes: " + postLikeRepository.count());
        } else {
            System.out.println("Skipping Post/Comment/Like seeding due to missing prerequisite User/Pet data.");
        }


        System.out.println("Seed data loading complete.");
    }
}
