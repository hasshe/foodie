package com.hasshe.foodie.service.api;

import com.hasshe.foodie.domain.UserDomain;
import com.hasshe.foodie.domain.UserIconDomain;
import com.hasshe.foodie.dto.ChangePasswordDisplay;
import com.hasshe.foodie.dto.RegisterUserDisplay;
import com.hasshe.foodie.dto.UpdateProfileDisplay;

import java.util.List;
import java.util.Optional;

public interface UserService {

    UserDomain registerUser(RegisterUserDisplay registerUserDisplay);

    Optional<UserDomain> findByUsername(String username);

    List<UserIconDomain> listAvailableIcons();

    UserDomain updateProfile(String currentUsername, UpdateProfileDisplay updateProfileDisplay);

    void changePassword(String username, ChangePasswordDisplay changePasswordDisplay);

    UserDomain setDefaultGroup(String username, Long groupId);
}
