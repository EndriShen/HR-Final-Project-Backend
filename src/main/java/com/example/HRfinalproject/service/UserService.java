package com.example.HRfinalproject.service;

import com.example.HRfinalproject.dto.userDto.CreateUserRequest;
import com.example.HRfinalproject.dto.userDto.UpdateUserRequest;
import com.example.HRfinalproject.entity.User;
import com.example.HRfinalproject.enums.UserRoles;
import com.example.HRfinalproject.exceptions.NotUniqueException;
import com.example.HRfinalproject.exceptions.UserNotFoundException;
import com.example.HRfinalproject.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public ResponseEntity<CreateUserRequest> createUser(CreateUserRequest createdUser) throws Exception{
        if(!isUsernameUnique(createdUser.getUsername())){
            throw new NotUniqueException("Username must be unique");
        }
        User newUser= User.builder()
                .firstName(createdUser.getFirstName())
                .lastName(createdUser.getLastName())
                .username(createdUser.getUsername())
                .password(createdUser.getPassword())
                .role(UserRoles.USER)
                .createdAt(LocalDate.now())
                .createdBy(createdUser.getUsername())
                .modifiedAt(LocalDate.now())
                .modifiedBy(createdUser.getUsername())
                .daysOff(20).build();
        userRepository.save(newUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    public ResponseEntity<UpdateUserRequest> updateUser(Long userId,Long managerId,UpdateUserRequest user) throws Exception {
        Optional<User> userToBeUpdated=userRepository.findById(userId);
        Optional<User> manager=userRepository.findById(managerId);
        if (userToBeUpdated.isEmpty()){throw new UserNotFoundException("User with id:"+userId+" doesnt exist");}
        else if (manager.isEmpty()){throw new UserNotFoundException("Manager with id:"+managerId+" doesnt exist");}
        else {
            userToBeUpdated.get().setFirstName(user.getFirstName());
            userToBeUpdated.get().setLastName(user.getLastName());
            userToBeUpdated.get().setUsername(user.getUsername());
            userToBeUpdated.get().setModifiedBy(manager.get().getUsername());
            userToBeUpdated.get().setModifiedAt(LocalDate.now());
            userRepository.save(userToBeUpdated.get());
            return ResponseEntity.status(HttpStatus.OK).body(user);
        }
    }

    public ResponseEntity deleteUser(Long id) throws EntityNotFoundException {
        Optional<User> deleteUser=userRepository.findById(id);
        if (deleteUser.isEmpty()){throw new EntityNotFoundException("User with id: "+id+"doesnt exist");}
        userRepository.deleteById(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    public ResponseEntity<User> getUserByCredentials(String username, String password) {
        User user = userRepository.findByUsernameAndPassword(username, password);
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    public boolean isUsernameUnique(String username) {
        User user = userRepository.findByUsername(username);
        return user == null;
    }

    public Optional<User> getUserById(Long id){
        return userRepository.findById(id);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
