package com.learning.blogPlatform.config;

import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

@Configuration
public class KafkaSSLConfig {

    @PostConstruct
    public void extractCertificates() {
        try {
            String keystoreBase64 = System.getenv("KAFKA_KEYSTORE_BASE64");
            String truststoreBase64 = System.getenv("KAFKA_TRUSTSTORE_BASE64");

            if (keystoreBase64 != null && !keystoreBase64.isEmpty()) {
                byte[] keystoreBytes = Base64.getDecoder().decode(keystoreBase64);
                Path keystorePath = Files.createTempFile("kafka-keystore", ".p12");
                try (FileOutputStream fos = new FileOutputStream(keystorePath.toFile())) {
                    fos.write(keystoreBytes);
                }
                System.setProperty("kafka.keystore.location", keystorePath.toString());
                System.setProperty("KAFKA_KEYSTORE_LOCATION", keystorePath.toString());
            }

            if (truststoreBase64 != null && !truststoreBase64.isEmpty()) {
                byte[] truststoreBytes = Base64.getDecoder().decode(truststoreBase64);
                Path truststorePath = Files.createTempFile("kafka-truststore", ".jks");
                try (FileOutputStream fos = new FileOutputStream(truststorePath.toFile())) {
                    fos.write(truststoreBytes);
                }
                System.setProperty("kafka.truststore.location", truststorePath.toString());
                System.setProperty("KAFKA_TRUSTSTORE_LOCATION", truststorePath.toString());
            }
        } catch (Exception e) {
            System.err.println("Failed to extract Kafka SSL certificates: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
