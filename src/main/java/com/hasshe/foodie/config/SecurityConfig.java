package com.hasshe.foodie.config;

import com.hasshe.foodie.constants.SecurityConstants;
import com.hasshe.foodie.views.LoginView;
import com.vaadin.flow.spring.security.VaadinWebSecurity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.CookieClearingLogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.util.Assert;

import java.time.Duration;
import java.util.function.Consumer;

@Configuration
@EnableWebSecurity
class SecurityConfig extends VaadinWebSecurity {

    private final UserDetailsService userDetailsService;
    private final String rememberMeKey;

    SecurityConfig(
            UserDetailsService userDetailsService,
            @Value("${foodie.security.remember-me-key}") String rememberMeKey
    ) {
        Assert.notNull(userDetailsService, "userDetailsService must not be null");
        Assert.hasText(rememberMeKey, "rememberMeKey must not be blank");
        this.userDetailsService = userDetailsService;
        this.rememberMeKey = rememberMeKey;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        super.configure(http);
        setLoginView(http, LoginView.class);
        http.rememberMe(rememberMe -> rememberMe
                .key(rememberMeKey)
                .userDetailsService(userDetailsService)
                .rememberMeCookieName(SecurityConstants.REMEMBER_ME_COOKIE_NAME)
                .tokenValiditySeconds((int) Duration.ofDays(SecurityConstants.REMEMBER_ME_VALIDITY_DAYS).toSeconds())
                .alwaysRemember(true));
        http.logout(logout -> logout.deleteCookies(SecurityConstants.REMEMBER_ME_COOKIE_NAME));
    }

    @Override
    protected void addLogoutHandlers(Consumer<LogoutHandler> logoutHandlerConsumer) {
        logoutHandlerConsumer.accept(new CookieClearingLogoutHandler(SecurityConstants.REMEMBER_ME_COOKIE_NAME));
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
