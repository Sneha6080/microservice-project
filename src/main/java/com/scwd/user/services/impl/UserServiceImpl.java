package com.scwd.user.services.impl;

import com.scwd.user.entities.Hotel;
import com.scwd.user.entities.Ratings;
import com.scwd.user.entities.User;
import com.scwd.user.exceptions.ResourceNotFoundException;
import com.scwd.user.external.services.HotelService;
import com.scwd.user.payloads.CommonResponse;
import com.scwd.user.repositories.UserRepository;
import com.scwd.user.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private HotelService hotelService;

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
            ArrayList<Ratings> ratingsOfUser = restTemplate.getForObject("http://RATINGSERVICE/ratings/users/"+ user.getUserId(), ArrayList.class);
            logger.info("Ratings for user {} : {}", user.getUserId(), ratingsOfUser);
            user.setRatings(ratingsOfUser);
        }
        return users;
    }

    @Override
    public User getUser(String userId) {

        // Get user from database with the help of UserRepository
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User with given ID is not found on server !!"));

        // Fetch ratings of the above user from Rating Service
        Ratings[] ratingsOfUser = restTemplate.getForObject(
                "http://RATINGSERVICE/ratings/users/" + user.getUserId(),
                Ratings[].class
        );
        logger.info("Ratings received from Rating Service: {}", ratingsOfUser);

        List<Ratings> ratings = ratingsOfUser != null
                ? Arrays.asList(ratingsOfUser)
                : Collections.emptyList();

        // For each rating, fetch the corresponding hotel from Hotel Service
        List<Ratings> ratingsList = ratings.stream()
                .map(rating -> {
//                    ResponseEntity<Hotel> responseEntity = restTemplate.getForEntity(
//                            "http://HOTELSERVICE/hotels/" + rating.getHotelId(),
//                            Hotel.class
//                    );
                    Hotel hotel = hotelService.getHotel(rating.getHotelId());

                    rating.setHotel(hotel);

                    // Return the current rating
                    return rating;
                })
                .collect(Collectors.toList());
        // Set ratings inside user
        user.setRatings(ratingsList);
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
