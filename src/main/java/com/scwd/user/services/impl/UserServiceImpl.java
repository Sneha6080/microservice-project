package com.scwd.user.services.impl;

import com.scwd.user.entities.Ratings;
import com.scwd.user.entities.User;
import com.scwd.user.exceptions.ResourceNotFoundException;
import com.scwd.user.payloads.CommonResponse;
import com.scwd.user.repositories.UserRepository;
import com.scwd.user.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RestTemplate restTemplate;

    private Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

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
        List<User> users = userRepository.findAll();
        for(User user : users){
            ArrayList<Ratings> ratingsOfUser = restTemplate.getForObject("http://localhost:8083/ratings/users/"+ user.getUserId(), ArrayList.class);
            logger.info("Ratings for user {} : {}", user.getUserId(), ratingsOfUser);
            user.setRatings(ratingsOfUser);
        }
        return users;
    }

    @Override
    public User getUser(String userId) {
        //get user from database with the help of user repository
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User with given ID is not found on server !!"));
        //fetch rating of the above user from Rating service
        //http://localhost:8083/ratings/users/30269404-bf22-45bd-aa49-8b815ff9f477

        ArrayList<Ratings> ratingsOfUser = restTemplate.getForObject("http://localhost:8083/ratings/users/"+ user.getUserId(), ArrayList.class);
        logger.info("{} ", ratingsOfUser);

        List<Ratings> ratingsList = ratingsOfUser.stream().map(ratings -> {
            //api call to hotel service to get the hotel
            //set the hotel rating
            //return the rating
            return ratings;
        }).collect(Collectors.toList());
        user.setRatings(ratingsOfUser);
        return user;
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
