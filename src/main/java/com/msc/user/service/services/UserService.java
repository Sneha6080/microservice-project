package com.msc.user.service.services;

import com.msc.user.service.entities.User;
import com.msc.user.service.payloads.CommonResponse;

import java.util.List;

public interface UserService {

    //User operations

    //Create user
    User createUser(User user);

    //Update User
    User updateUser(User user, String userId);

    //Get all users
    List<User> getAllUsers();

    //Get single user
    User getUser(String userId);

    //Delete user
    CommonResponse deleteUser(String userId);

}
