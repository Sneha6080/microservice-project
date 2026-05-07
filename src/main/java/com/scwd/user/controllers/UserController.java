package com.scwd.user.controllers;

import com.scwd.user.entities.User;
import com.scwd.user.payloads.CommonResponse;
import com.scwd.user.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;

    //Create User
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user){
       User user1 = userService.createUser(user);
       return ResponseEntity.status(HttpStatus.CREATED).body(user1);
    }

    //Update User
    @PutMapping("/{userId}")
    public ResponseEntity<User> updateUser(@RequestBody User user, @PathVariable String userId){
        User user1 = userService.updateUser(user, userId);
        return ResponseEntity.status(HttpStatus.OK).body(user1);
    }
    //Get User
    @GetMapping("/{userId}")
    public ResponseEntity<User> getUser(@PathVariable String userId){
        User user = userService.getUser(userId);
        return ResponseEntity.ok(user);
    }

    //Get all user
    @GetMapping
    public ResponseEntity<List<User>> getAllUser(){
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<CommonResponse> deleteUser(@PathVariable String userId){
        return ResponseEntity.ok(userService.deleteUser(userId));
    }
}
