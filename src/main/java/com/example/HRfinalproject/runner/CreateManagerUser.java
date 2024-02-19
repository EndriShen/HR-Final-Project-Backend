package com.example.HRfinalproject.runner;

import com.example.HRfinalproject.entity.User;
import com.example.HRfinalproject.enums.UserRoles;
import com.example.HRfinalproject.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class CreateManagerUser implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        User manager = new User("Endri", "Shenplaku", "e.shen", "teamsystem", UserRoles.MANAGER, 20, LocalDate.now(), "Endri", LocalDate.now(), "Endri");
        userRepository.save(manager);
    }
}