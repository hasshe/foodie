package com.hasshe.foodie.controller;

import com.hasshe.foodie.domain.UserDomain;
import com.hasshe.foodie.dto.RegisterUserDisplay;
import com.hasshe.foodie.exception.ValidationException;
import com.hasshe.foodie.service.api.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegistrationControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private RegistrationController registrationController;

    @Test
    void given_validDisplay_when_registerUser_then_returnsServiceResult() {
        RegisterUserDisplay request = new RegisterUserDisplay("chef123", "rawPassword", "Chef");
        UserDomain expected = new UserDomain(1L, "chef123", "Chef", LocalDateTime.now(), LocalDateTime.now());
        when(userService.registerUser(request)).thenReturn(expected);

        UserDomain result = registrationController.registerUser(request);

        assertThat(result).isEqualTo(expected);
    }

    @Test
    void given_anotherValidDisplay_when_registerUser_then_returnsServiceResult() {
        RegisterUserDisplay request = new RegisterUserDisplay("foodie99", "rawPassword", "Foodie");
        UserDomain expected = new UserDomain(2L, "foodie99", "Foodie", LocalDateTime.now(), LocalDateTime.now());
        when(userService.registerUser(request)).thenReturn(expected);

        UserDomain result = registrationController.registerUser(request);

        assertThat(result).isEqualTo(expected);
    }

    @Test
    void given_nullDisplay_when_registerUser_then_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> registrationController.registerUser(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void given_serviceThrowsValidationException_when_registerUser_then_propagatesException() {
        RegisterUserDisplay request = new RegisterUserDisplay("chef123", "rawPassword", "Chef");
        when(userService.registerUser(request)).thenThrow(new ValidationException("Username already taken: chef123"));

        assertThatThrownBy(() -> registrationController.registerUser(request))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void given_validDisplay_when_registerUser_then_callsServiceExactlyOnce() {
        RegisterUserDisplay request = new RegisterUserDisplay("chef123", "rawPassword", "Chef");
        UserDomain expected = new UserDomain(1L, "chef123", "Chef", LocalDateTime.now(), LocalDateTime.now());
        when(userService.registerUser(request)).thenReturn(expected);

        registrationController.registerUser(request);

        verify(userService, times(1)).registerUser(request);
    }
}
