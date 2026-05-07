package com.scwd.user.services.impl;

import com.scwd.user.entities.User;
import com.scwd.user.exceptions.ResourceNotFoundException;
import com.scwd.user.payloads.CommonResponse;
import com.scwd.user.repositories.UserRepository;
import com.scwd.user.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public User createUser(User user) {
        //Generate unique userID
        String randomUserId = UUID.randomUUID().toString();
        user.setUserId(randomUserId);
        return userRepository.save(user);
    }

    @Override
    public User updateUser(User user, String userId) {
        User existingUser = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User with given ID is not found on server !!"));
        existingUser.setName(user.getName());
        existingUser.setEmail(user.getEmail());
        existingUser.setAbout(user.getAbout());
        return userRepository.save(existingUser);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUser(String userId) {
        return userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User with given ID is not found on server !!"));
    }

    @Override
    public CommonResponse deleteUser(String userId) {
    User user = userRepository.findById(userId).orElseThrow(()-> new ResourceNotFoundException("User with given ID is not found on server !!"));
    userRepository.delete(user);
        return new CommonResponse(
                "User deleted successfully",
                true
        );
    }
}
