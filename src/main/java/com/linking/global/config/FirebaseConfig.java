package com.linking.global.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.annotation.PostConstruct;
import java.io.IOException;

@Slf4j
@Configuration
public class FirebaseConfig {

    private static final String firebaseConfigPath = "/firebase/linking-32bc1-firebase-adminsdk-c2paj-770ba0d093.json";


    @PostConstruct
    public void init() {
        try {
            ClassPathResource account = new ClassPathResource(firebaseConfigPath);

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(account.getInputStream()))
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                log.info("Firebase init");
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
