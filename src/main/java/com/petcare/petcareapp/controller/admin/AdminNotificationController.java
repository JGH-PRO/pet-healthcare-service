package com.petcare.petcareapp.controller.admin;

import com.petcare.petcareapp.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/notifications")
@Tag(name = "Admin: Notification Management", description = "APIs for administrators to manage and test notifications.")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')") // Ensure role prefix matches UserDetailsServiceImpl if it adds ROLE_
public class AdminNotificationController {

    @Autowired
    private NotificationService notificationService;

    @PostMapping("/trigger-vaccination-reminders")
    @Operation(summary = "Manually trigger conceptual vaccination reminders (Admin)",
               description = "This endpoint calls the conceptual `sendVaccinationReminders` method in `NotificationService`. " +
                             "For testing, provide an optional username to target their devices, or a specific device token. " +
                             "This is for testing the notification sending mechanism; actual scheduling is not implemented.")
    @Parameter(name = "testUserUsername", description = "Username of a test user to send reminders to their devices. (Optional)", required = false, example = "userone")
    @Parameter(name = "testDeviceToken", description = "A specific device token to send a test reminder to. (Optional, overrides username if both provided)", required = false, example = "specific-fcm-device-token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Conceptual reminder process triggered successfully. Check server logs for details of simulated sends."),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden - User does not have ADMIN role")
    })
    public ResponseEntity<String> triggerVaccinationReminders(
            @RequestParam(required = false) String testUserUsername,
            @RequestParam(required = false) String testDeviceToken) {

        // Note: The actual sending capability depends on FCM setup and valid tokens.
        // This call will log its actions.
        notificationService.sendVaccinationReminders(testUserUsername, testDeviceToken);
        String message = "Conceptual vaccination reminder process triggered. Check logs for details.";
        if (testUserUsername != null) message += " Targeted User: " + testUserUsername + ".";
        if (testDeviceToken != null) message += " Targeted Token: " + testDeviceToken + ".";
        return ResponseEntity.ok(message);
    }
}
