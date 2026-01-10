package com.dibimbing.apiassignment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class ApiAssignmentDay27Application {

    public static void main(String[] args) {
        SpringApplication.run(ApiAssignmentDay27Application.class, args);
    }

}
