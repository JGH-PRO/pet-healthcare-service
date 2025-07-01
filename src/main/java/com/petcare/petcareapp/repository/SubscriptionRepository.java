package com.petcare.petcareapp.repository;

import com.petcare.petcareapp.domain.Subscription;
import com.petcare.petcareapp.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    List<Subscription> findByUserAndIsActiveTrueOrderByNextDeliveryDateAsc(User user);
    Optional<Subscription> findByIdAndUser(Long id, User user);
    // For checking if a user already has an active subscription for a product
    Optional<Subscription> findByUserAndProductIdAndIsActiveTrue(User user, Long productId);
}
