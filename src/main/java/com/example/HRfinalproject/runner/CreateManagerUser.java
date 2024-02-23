package com.example.HRfinalproject.runner;

import com.example.HRfinalproject.entity.User;
import com.example.HRfinalproject.enums.UserRoles;
import com.example.HRfinalproject.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

import static org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers.ignoreCase;

@Component
public class CreateManagerUser implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        ExampleMatcher modelMatcher = ExampleMatcher.matching()
                .withIgnorePaths("id")
                .withMatcher("username", ignoreCase())
                .withMatcher("role", ignoreCase())
                .withMatcher("firstName",ignoreCase())
                .withMatcher("lastName",ignoreCase())
                .withMatcher("DaysOff",ignoreCase());

        User manager=User.builder()
                .role(UserRoles.MANAGER)
                .username("e.shen")
                .firstName("Endri")
                .lastName("Shen")
                .daysOff(0)
                .build();
        Example<User> example = Example.of(manager, modelMatcher);
        if (!userRepository.exists(example)){
            manager.setCreatedBy("Endri");
            manager.setCreatedAt(LocalDate.now());
            manager.setPassword("teamsystem");
            manager.setModifiedAt(LocalDate.now());
            manager.setModifiedBy("Endri");
            userRepository.save(manager);
        }
    }
}