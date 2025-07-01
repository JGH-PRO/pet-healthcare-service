package com.petcare.petcareapp.controller;

import com.petcare.petcareapp.dto.subscription.CreateSubscriptionRequestDto;
import com.petcare.petcareapp.dto.subscription.SubscriptionDto;
import com.petcare.petcareapp.dto.subscription.UpdateSubscriptionRequestDto;
import com.petcare.petcareapp.service.SubscriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api")
@Tag(name = "Subscription Management", description = "APIs for managing product subscriptions")
@SecurityRequirement(name = "bearerAuth")
public class SubscriptionController {

    @Autowired
    private SubscriptionService subscriptionService;

    @PostMapping("/subscriptions")
    @Operation(summary = "Create a new subscription", description = "Creates a new product subscription for the authenticated user.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Subscription created successfully", content = @Content(schema = @Schema(implementation = SubscriptionDto.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input, product not found, or insufficient stock"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<SubscriptionDto> createSubscription(@Valid @RequestBody CreateSubscriptionRequestDto requestDto,
                                                              @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {
        try {
            SubscriptionDto createdSubscription = subscriptionService.createSubscription(requestDto, userDetails.getUsername());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdSubscription);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @GetMapping("/users/me/subscriptions")
    @Operation(summary = "List active subscriptions for current user", description = "Retrieves a list of all active subscriptions for the authenticated user.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved subscriptions", content = @Content(mediaType="application/json", schema = @Schema(implementation = SubscriptionDto.class))) // Added mediaType
    })
    public ResponseEntity<List<SubscriptionDto>> getActiveSubscriptionsForCurrentUser(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {
        List<SubscriptionDto> subscriptions = subscriptionService.getActiveSubscriptionsForUser(userDetails.getUsername());
        return ResponseEntity.ok(subscriptions);
    }

    @GetMapping("/subscriptions/{subscriptionId}")
    @Operation(summary = "Get a specific subscription by ID", description = "Retrieves details of a specific subscription, if it belongs to the authenticated user.")
    @Parameter(name = "subscriptionId", description = "ID of the subscription to retrieve", required = true)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved subscription", content = @Content(schema = @Schema(implementation = SubscriptionDto.class))),
        @ApiResponse(responseCode = "404", description = "Subscription not found or does not belong to user"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<SubscriptionDto> getSubscriptionById(@PathVariable Long subscriptionId,
                                                               @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {
        try {
            SubscriptionDto subscriptionDto = subscriptionService.getSubscriptionByIdAndUser(subscriptionId, userDetails.getUsername());
            return ResponseEntity.ok(subscriptionDto);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }

    @PutMapping("/subscriptions/{subscriptionId}")
    @Operation(summary = "Update a subscription", description = "Updates details of an existing subscription (e.g., quantity, frequency, shipping address, active status).")
    @Parameter(name = "subscriptionId", description = "ID of the subscription to update", required = true)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Subscription updated successfully", content = @Content(schema = @Schema(implementation = SubscriptionDto.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input or insufficient stock for update"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "Subscription not found or does not belong to user")
    })
    public ResponseEntity<SubscriptionDto> updateSubscription(@PathVariable Long subscriptionId,
                                                              @Valid @RequestBody UpdateSubscriptionRequestDto requestDto,
                                                              @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {
        try {
            SubscriptionDto updatedSubscription = subscriptionService.updateSubscription(subscriptionId, requestDto, userDetails.getUsername());
            return ResponseEntity.ok(updatedSubscription);
        } catch (AccessDeniedException e) { // Catch specific AccessDeniedException
             throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage(), e);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @DeleteMapping("/subscriptions/{subscriptionId}")
    @Operation(summary = "Cancel (deactivate) a subscription", description = "Marks a subscription as inactive. Does not permanently delete the record.")
    @Parameter(name = "subscriptionId", description = "ID of the subscription to cancel", required = true)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Subscription canceled successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "Subscription not found or does not belong to user")
    })
    public ResponseEntity<Void> cancelSubscription(@PathVariable Long subscriptionId,
                                                   @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {
        try {
            subscriptionService.cancelSubscription(subscriptionId, userDetails.getUsername());
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }
}
