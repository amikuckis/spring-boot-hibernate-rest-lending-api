package io.fourfinanceit.app.controller;

import io.fourfinanceit.app.exception.ResourceNotFoundException;
import io.fourfinanceit.app.exception.UserAlreadyExistsException;
import io.fourfinanceit.app.model.domain.User;
import io.fourfinanceit.app.service.UserService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class UserControllerTest {

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private User user;

    @Before
    public void setUp() {
        user = new User();
        user.setName("John");
        user.setSurname("Smith");
        user.setPhoneNumber("22222");
        user.setNationalIdentityNumber("natID");

        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetUser() {
        when(userService.findById(anyLong())).thenReturn(Optional.ofNullable(user));

        User actual = userController.getUser(1L);

        assertNotNull(actual);
        verify(userService, times(1)).findById(anyLong());
    }

    @Test
    public void testGetUserWhenNotFound() {
        when(userService.findById(anyLong())).thenReturn(Optional.empty());

        exception.expect(ResourceNotFoundException.class);
        exception.expectMessage("User not found with id : '1'");

        userController.getUser(1L);
    }

    @Test
    public void testCreateUser() {
        userController.createUser(user);

        verify(userService, times(1)).saveUser(any());
    }

    @Test
    public void testCreateUserIfAlreadyExists() {
        when(userService.findByNationalIdentityNumber(anyString())).thenReturn(Optional.ofNullable(user));

        exception.expect(UserAlreadyExistsException.class);
        exception.expectMessage("User with national identity number : 'natID' already exists");

        userController.createUser(user);
    }

}
