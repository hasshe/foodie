package com.hasshe.foodie.service.impl;

import com.hasshe.foodie.constants.SecurityConstants;
import com.hasshe.foodie.db.api.UserDb;
import com.hasshe.foodie.db.entity.UserEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;

@Service
class AppUserDetailsService implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(AppUserDetailsService.class);

    private final UserDb userDb;

    AppUserDetailsService(UserDb userDb) {
        this.userDb = userDb;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Assert.hasText(username, "username must not be blank");
        log.debug("Loading user details for username {}", username);
        UserEntity userEntity = userDb.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("No user found with username: " + username));
        return new User(
                userEntity.getUsername(),
                userEntity.getPassword(),
                List.of(new SimpleGrantedAuthority(SecurityConstants.ROLE_USER))
        );
    }
}
