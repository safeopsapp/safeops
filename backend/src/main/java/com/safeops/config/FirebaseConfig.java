package com.safeops.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Configuration
@Profile("!test")
public class FirebaseConfig {

    private static final Logger log = LoggerFactory.getLogger(FirebaseConfig.class);

    @Value("${safeops.firebase.credentials-path:}")
    private String credentialsPath;

    @Value("${safeops.firebase.credentials-json:}")
    private String credentialsJson;

    @Bean
    public FirebaseApp firebaseApp() throws IOException {
        if (!FirebaseApp.getApps().isEmpty()) {
            log.info("Firebase already initialized, reusing existing app");
            return FirebaseApp.getInstance();
        }

        GoogleCredentials credentials;

        if (credentialsJson != null && !credentialsJson.isBlank()) {
            log.info("Initializing Firebase from credentials JSON (environment variable)");
            try (InputStream stream = new ByteArrayInputStream(credentialsJson.getBytes(StandardCharsets.UTF_8))) {
                credentials = GoogleCredentials.fromStream(stream);
            }
        } else if (credentialsPath != null && !credentialsPath.isBlank()) {
            log.info("Initializing Firebase from credentials file: {}", credentialsPath);
            try (FileInputStream stream = new FileInputStream(credentialsPath)) {
                credentials = GoogleCredentials.fromStream(stream);
            }
        } else {
            log.info("No Firebase credentials configured - checking Application Default Credentials");
            try {
                credentials = GoogleCredentials.getApplicationDefault();
            } catch (IOException e) {
                log.warn("Application Default Credentials not found; using fallback mock credentials for local/testing");
                credentials = GoogleCredentials.create(
                    new com.google.auth.oauth2.AccessToken("mock-token", new java.util.Date(System.currentTimeMillis() + 3600000))
                );
            }
        }

        FirebaseOptions options = FirebaseOptions.builder()
            .setCredentials(credentials)
            .setProjectId("safeops-local")
            .build();

        return FirebaseApp.initializeApp(options);
    }
}
