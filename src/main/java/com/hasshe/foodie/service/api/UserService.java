package com.hasshe.foodie.service.api;

import com.hasshe.foodie.domain.UserDomain;
import com.hasshe.foodie.dto.RegisterUserDisplay;

import java.util.Optional;

public interface UserService {

    UserDomain registerUser(RegisterUserDisplay registerUserDisplay);

    Optional<UserDomain> findByUsername(String username);
}
