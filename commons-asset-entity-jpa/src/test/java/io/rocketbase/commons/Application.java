package io.rocketbase.commons;

import io.rocketbase.commons.service.ReferenceHashMigrationService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application {

    @Bean
    public ReferenceHashMigrationService referenceHashMigrationService() {
        return new ReferenceHashMigrationService();
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(Application.class, args);
    }

}