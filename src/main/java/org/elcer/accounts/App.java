package org.elcer.accounts;

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

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @Bean
    protected CommandLineRunner commandLineRunner() {
        return args -> System.out.println("Application started with profiles " +
                String.join(",", environment.getActiveProfiles()));
    }
}
