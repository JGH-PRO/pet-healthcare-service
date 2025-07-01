package com.petcare.petcareapp.service;

import com.petcare.petcareapp.domain.Product;
import com.petcare.petcareapp.domain.Subscription;
import com.petcare.petcareapp.domain.User;
import com.petcare.petcareapp.domain.model.SubscriptionFrequency;
import com.petcare.petcareapp.dto.subscription.CreateSubscriptionRequestDto;
import com.petcare.petcareapp.dto.subscription.SubscriptionDto;
import com.petcare.petcareapp.dto.subscription.UpdateSubscriptionRequestDto;
import com.petcare.petcareapp.repository.ProductRepository;
import com.petcare.petcareapp.repository.SubscriptionRepository;
import com.petcare.petcareapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.AccessDeniedException; // Added for ownership check

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SubscriptionService {

    @Autowired private SubscriptionRepository subscriptionRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private ProductRepository productRepository;

    private User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    private LocalDate calculateNextDeliveryDate(LocalDate fromDate, SubscriptionFrequency frequency) {
        if (frequency == null) return fromDate; // Or throw error, or default
        return fromDate.plusDays(frequency.getDays());
    }

    @Transactional
    public SubscriptionDto createSubscription(CreateSubscriptionRequestDto requestDto, String username) {
        User user = getUserByUsername(username);
        Product product = productRepository.findById(requestDto.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + requestDto.getProductId()));

        Optional<Subscription> existingSub = subscriptionRepository.findByUserAndProductIdAndIsActiveTrue(user, product.getId());
        if (existingSub.isPresent()) {
            throw new RuntimeException("User already has an active subscription for product: " + product.getName());
        }

        if (product.getStockQuantity() == null || product.getStockQuantity() < requestDto.getQuantity()) {
            throw new RuntimeException("Not enough stock for product: " + product.getName() + ". Available: " + (product.getStockQuantity() == null ? 0 : product.getStockQuantity()));
        }

        Subscription subscription = new Subscription();
        subscription.setUser(user);
        subscription.setProduct(product);
        subscription.setQuantity(requestDto.getQuantity());
        subscription.setFrequency(requestDto.getFrequency());
        subscription.setShippingAddress(requestDto.getShippingAddress());
        subscription.setStartDate(LocalDate.now());
        subscription.setNextDeliveryDate(calculateNextDeliveryDate(subscription.getStartDate(), requestDto.getFrequency()));
        subscription.setActive(true);

        product.setStockQuantity(product.getStockQuantity() - requestDto.getQuantity());
        productRepository.save(product);

        Subscription savedSubscription = subscriptionRepository.save(subscription);
        return mapToDto(savedSubscription);
    }

    @Transactional(readOnly = true)
    public List<SubscriptionDto> getActiveSubscriptionsForUser(String username) {
        User user = getUserByUsername(username);
        return subscriptionRepository.findByUserAndIsActiveTrueOrderByNextDeliveryDateAsc(user)
                .stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public SubscriptionDto getSubscriptionByIdAndUser(Long subscriptionId, String username) {
        User user = getUserByUsername(username);
        Subscription subscription = subscriptionRepository.findByIdAndUser(subscriptionId, user)
                .orElseThrow(() -> new RuntimeException("Subscription not found or does not belong to user"));
        return mapToDto(subscription);
    }

    @Transactional
    public SubscriptionDto updateSubscription(Long subscriptionId, UpdateSubscriptionRequestDto requestDto, String username) {
        User user = getUserByUsername(username);
        Subscription subscription = subscriptionRepository.findByIdAndUser(subscriptionId, user)
                .orElseThrow(() -> new RuntimeException("Subscription not found or does not belong to user"));

        boolean frequencyChanged = false;
        Product product = subscription.getProduct(); // Get product associated with subscription

        if (requestDto.getQuantity() != null) {
            int oldQuantity = subscription.getQuantity();
            int newQuantity = requestDto.getQuantity();
            int quantityChange = newQuantity - oldQuantity;

            if (quantityChange > 0 && (product.getStockQuantity() == null || product.getStockQuantity() < quantityChange)) {
                 throw new RuntimeException("Not enough stock to increase quantity for product: " + product.getName());
            }
            product.setStockQuantity(product.getStockQuantity() - quantityChange); // This will add back stock if quantityChange is negative
            productRepository.save(product);
            subscription.setQuantity(newQuantity);
        }
        if (requestDto.getFrequency() != null) {
            subscription.setFrequency(requestDto.getFrequency());
            frequencyChanged = true;
        }
        if (requestDto.getShippingAddress() != null) {
            subscription.setShippingAddress(requestDto.getShippingAddress());
        }
        if (requestDto.getIsActive() != null) {
            if (!subscription.isActive() && requestDto.getIsActive()) { // Reactivating
                 if(product.getStockQuantity() == null || product.getStockQuantity() < subscription.getQuantity()){
                     throw new RuntimeException("Cannot reactivate: Not enough stock for product " + product.getName());
                 }
                 product.setStockQuantity(product.getStockQuantity() - subscription.getQuantity());
                 productRepository.save(product);
            } else if (subscription.isActive() && !requestDto.getIsActive()) { // Deactivating
                 product.setStockQuantity(product.getStockQuantity() + subscription.getQuantity());
                 productRepository.save(product);
            }
            subscription.setActive(requestDto.getIsActive());
        }

        if (frequencyChanged && subscription.isActive()) {
            subscription.setNextDeliveryDate(calculateNextDeliveryDate(LocalDate.now(), subscription.getFrequency()));
        }

        Subscription updatedSubscription = subscriptionRepository.save(subscription);
        return mapToDto(updatedSubscription);
    }

    @Transactional
    public void cancelSubscription(Long subscriptionId, String username) {
        User user = getUserByUsername(username);
        Subscription subscription = subscriptionRepository.findByIdAndUser(subscriptionId, user)
                .orElseThrow(() -> new RuntimeException("Subscription not found or does not belong to user"));

        if (subscription.isActive()) {
            subscription.setActive(false);
            Product product = subscription.getProduct();
            product.setStockQuantity(product.getStockQuantity() + subscription.getQuantity());
            productRepository.save(product);
            subscriptionRepository.save(subscription);
        }
    }

    private SubscriptionDto mapToDto(Subscription sub) {
        return new SubscriptionDto(
                sub.getId(), sub.getUser().getId(), sub.getProduct().getId(), sub.getProduct().getName(),
                sub.getQuantity(), sub.getFrequency(), sub.getStartDate(), sub.getNextDeliveryDate(),
                sub.isActive(), sub.getShippingAddress(), sub.getCreatedAt(), sub.getUpdatedAt()
        );
    }
}
