package com.petcare.petcareapp.repository;

import com.petcare.petcareapp.domain.User; // Assuming this domain class exists
import com.petcare.petcareapp.domain.UserDevice; // Assuming this domain class exists
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserDeviceRepository extends JpaRepository<UserDevice, Long> {

    List<UserDevice> findByUser_Username(String username);

    List<UserDevice> findByUser(User user);

    Optional<UserDevice> findByUserAndDeviceToken(User user, String deviceToken);
}
