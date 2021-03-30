package com.project.springapirest;

import com.project.springapirest.model.Role;
import com.project.springapirest.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;

@SpringBootApplication
public class SpringApirestApplication implements CommandLineRunner {
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public static void main(String[] args) {
        SpringApplication.run(SpringApirestApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        String password = "Admin1234";

        for(int i = 0; i < 1; i++) {
            String passwordEncoded = bCryptPasswordEncoder.encode(password);

            System.out.println(passwordEncoded);
        }
    }
}
