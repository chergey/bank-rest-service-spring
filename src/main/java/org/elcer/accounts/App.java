package org.elcer.accounts;

import org.slf4j.Logger;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import javax.inject.Inject;

@SpringBootApplication
public class App {

    @Inject
    private Environment environment;

    @Inject
    private Logger logger;

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @Bean
    protected CommandLineRunner commandLineRunner() {
        return args -> logger.info("Application started with profiles {}",
                String.join(",", environment.getActiveProfiles()));
    }
}
