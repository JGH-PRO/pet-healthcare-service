package com.petcare.petcareapp.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {

    private static final Logger logger = LoggerFactory.getLogger(FirebaseConfig.class);

    @Value("${firebase.service-account-key-path:}")
    private String serviceAccountKeyPath;

    @PostConstruct
    public void initializeFirebaseApp() {
        try {
            if (serviceAccountKeyPath == null || serviceAccountKeyPath.trim().isEmpty()) {
                logger.warn("Firebase service account key path is not configured (firebase.service-account-key-path). Firebase Admin SDK will not be initialized.");
                return;
            }

            Resource resource = new FileSystemResource(serviceAccountKeyPath);
            if (!resource.exists()) {
                resource = new ClassPathResource(serviceAccountKeyPath);
            }

            if (!resource.exists()) {
                logger.error("Firebase service account key file not found at path: {}. Firebase Admin SDK initialization failed.", serviceAccountKeyPath);
                return;
            }

            InputStream serviceAccount = resource.getInputStream();

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                logger.info("Firebase Admin SDK initialized successfully.");
            } else {
                logger.info("Firebase Admin SDK already initialized.");
            }

        } catch (IOException e) {
            logger.error("IOException during Firebase Admin SDK initialization: {}", e.getMessage(), e);
        } catch (Exception e) {
            logger.error("An unexpected error occurred during Firebase Admin SDK initialization: {}", e.getMessage(), e);
        }
    }
}
