package com.example.HRfinalproject.controller;

import com.example.HRfinalproject.dto.userDto.CreateUserRequest;
import com.example.HRfinalproject.dto.userDto.UpdateUserRequest;
import com.example.HRfinalproject.entity.Timesheet;
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

//    @GetMapping("/login")
//    public ResponseEntity<User> getUserByCredentials
//            (@RequestParam("username") String username,
//             @RequestParam("password") String password){
//        return userService.login(username,password);
//    }

//    @PostMapping("/login")
//    public ResponseEntity<User> login(@RequestBody LoginRequest loginRequest) {
//        try {
//            User user = userService.login(loginRequest.getUsername(), loginRequest.getPassword());
//            return ResponseEntity.ok(user);
//        } catch (UserNotFoundException e) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//        }
//    }

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
            //return ResponseEntity.ok("Username is unique.");
        } else {
            return isUsernameTaken;
            //return ResponseEntity.badRequest().body("Username is already taken.");
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

//    @PostMapping("/register")
//    public User register(@RequestBody User user) {
//        return userService.createUser(user);
//    }
//
//    @PostMapping("/login")
//    public ResponseEntity<GetUserResponse> login(@RequestBody LoginRequest loginRequest) throws UserNotFoundException {
//        User user = userService.findUserByUsername(loginRequest.getUsername(), loginRequest.getPassword());
//        GetUserResponse response = new GetUserResponse(
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
//        return ResponseEntity.ok(response);
//    }
//
//    @GetMapping("/users")
//    public List<User> getAllUsers() {
//        return userService.getAllUsers();
//    }
}
