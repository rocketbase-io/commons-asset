package io.rocketbase.commons;

import io.rocketbase.commons.service.ReferenceHashMigrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.persistence.EntityManager;

@SpringBootApplication
public class Application {

    @Bean
    public ReferenceHashMigrationService referenceHashMigrationService(@Autowired EntityManager em) {
        return new ReferenceHashMigrationService(em);
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(Application.class, args);
    }

}