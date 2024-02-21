package com.example.HRfinalproject.service;

import com.example.HRfinalproject.dto.userDto.CreateUserRequest;
import com.example.HRfinalproject.dto.userDto.UpdateUserRequest;
import com.example.HRfinalproject.entity.Timesheet;
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
import java.util.Date;
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
                .daysOff(20).build();
        userRepository.save(newUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

//    public ResponseEntity<UpdateUserRequest> updateUser(Long id, UpdateUserRequest user) throws UserNotFoundException {
//        Optional<User> updatedUser=userRepository.findById(id);
//        if (updatedUser.isEmpty()){throw new EntityNotFoundException("User with id: "+id+"doesnt exist");}
//        else{
//            updatedUser.get().setFirstName(user.getFirstName());
//            updatedUser.get().setLastName(user.getLastName());
//            updatedUser.get().setUsername(user.getUsername());
//            userRepository.save(updatedUser.get());
//            return ResponseEntity.status(HttpStatus.OK).body(user);
//        }
//    }

    public ResponseEntity<UpdateUserRequest> updateUser(Long userId,Long managerId,UpdateUserRequest user) throws Exception {
        Optional<User> userToBeUpdated=userRepository.findById(userId);
        Optional<User> manager=userRepository.findById(managerId);
        if (userToBeUpdated.isEmpty()){throw new EntityNotFoundException("User with id:"+userId+" doesnt exist");}
        else if (manager.isEmpty()){throw new EntityNotFoundException("Manager with id:"+managerId+" doesnt exist");}
        else{
            if(!isUsernameUnique(userToBeUpdated.get().getUsername())){
                throw new NotUniqueException("Username must be unique");
            };
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
        return ResponseEntity.status(HttpStatus.OK).body("User Deleted");
    }

    public ResponseEntity<User> getUserByCredentials(String username, String password) {
        User user = userRepository.findByUsernameAndPassword(username, password);
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    public ResponseEntity<Integer> updateUserDaysOff(Long userId,Integer daysToBeRemoved) throws Exception {
        Optional<User> user=userRepository.findById(userId);
        if(user.isEmpty()){throw new UserNotFoundException(("User with id:"+userId+"doesnt exist"));}
        user.get().setDaysOff(user.get().getDaysOff()-daysToBeRemoved);
        userRepository.save(user.get());
        return ResponseEntity.ok(user.get().getDaysOff());
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

    //    public ResponseEntity<User> login(String username,String password){
//        Optional<User> userCredentials=userRepository.findByUsernameAndPassword(username, password);
//        if (userCredentials.isPresent()){
//                return ResponseEntity.ok(userCredentials.get());
//        }
//        return null;
//    }

//    public ResponseEntity<User> login(String username,String password){
//        Optional<User> userCredentials=userRepository.findByUsername(username);
//        if (userCredentials.isPresent()){
//            if(password == userCredentials.get().getPassword()) {
//                return ResponseEntity.ok(userCredentials.get());
//            }
//        }
//        return null;
//    }

//    public User login(String username, String password) throws UserNotFoundException {
//        Optional<User> optionalUser = userRepository.findByUsernameAndPassword(username, password);
//        return optionalUser.orElseThrow(() -> new UserNotFoundException("Invalid credentials"));
//    }

//    public List<User> getAllUsers() {
//        return userRepository.findAll();
//    }
//
//    public User createUser(User user) {
//        // Set default number of vacations
//        user.setDaysOff(20);
//        return userRepository.save(user);
//    }
//
//    public GetUserResponse login(String username, String password) throws UserNotFoundException {
//        User user = userRepository.findByUsernameAndPassword(username, password).orElseThrow(() -> new UserNotFoundException("User with " + username + " not found"));
//        return new GetUserResponse(
//                user.getFirstName(),
//                user.getLastName(),
//                user.getUsername(),
//                user.getPassword(),
//                user.getRole(),
//                user.getDaysOff(),
//                user.getCreatedAt(),
//                user.getCreatedBy(),
//                user.getModifiedAt(),
//                user.getModifiedBy()
//        );
//    }
//
//    public User findUserByUsername(String username, String password) throws UserNotFoundException {
//        return userRepository.findByUsernameAndPassword(username, password)
//                .orElseThrow(() -> new UserNotFoundException("User with " + username + " not found"));
//    }
}
