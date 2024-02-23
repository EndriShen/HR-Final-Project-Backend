package com.example.HRfinalproject.controller;

import com.example.HRfinalproject.dto.userDto.CreateUserRequest;
import com.example.HRfinalproject.dto.userDto.UpdateUserRequest;
import com.example.HRfinalproject.entity.User;
import com.example.HRfinalproject.exceptions.UserNotFoundException;
import com.example.HRfinalproject.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@AllArgsConstructor
@RequestMapping("/user")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/create")
    public ResponseEntity<CreateUserRequest> createUser(@RequestBody CreateUserRequest user) throws Exception {
        return userService.createUser(user);
    }

    @GetMapping("/login")
    public ResponseEntity<User> getUserByCredentials(
            @RequestParam("username") String username,
            @RequestParam("password") String password){
        return userService.getUserByCredentials(username, password);
    }

    @PatchMapping("/update/{userId}/{managerId}")
    public ResponseEntity<UpdateUserRequest> updateUser
            (@PathVariable("userId") Long userId,
             @PathVariable("managerId") Long managerId,
             @RequestBody UpdateUserRequest user) throws Exception {
        return userService.updateUser(userId, managerId,user);
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity deleteUser(@PathVariable("id") Long id) throws UserNotFoundException {
        return userService.deleteUser(id);
    }

    @GetMapping("check-username")
    public boolean isUsernameTaken(@RequestParam String username) {
        boolean isUsernameTaken = true;
        if (userService.isUsernameUnique(username)) {
            return !isUsernameTaken;
        } else {
            return isUsernameTaken;
        }
    }

    @GetMapping("/all-users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        Optional<User> user = userService.getUserById(id);
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
