package io.fourfinanceit.app.service;

import io.fourfinanceit.app.model.domain.User;
import io.fourfinanceit.app.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class UserServiceTest {

    @InjectMocks
    UserService userService;

    @Mock
    UserRepository userRepository;

    private User user;

    @Before
    public void setUp() {
        user = new User();

        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testSaveUser() {
        userService.saveUser(user);

        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void testFindByIdWhenUserFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));

        Optional<User> actual = userService.findById(1L);

        assertNotNull(actual);
        assertEquals(user, actual.get());

        verify(userRepository, times(1)).findById(anyLong());
    }

    @Test
    public void testFindByIdWhenUserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        Optional<User> actual = userService.findById(1L);

        assertNotNull(actual);
        assertFalse(actual.isPresent());

        verify(userRepository, times(1)).findById(anyLong());
    }

    @Test
    public void testFindByNationalIdentityNumberWhenUserFound() {
        when(userRepository.findByNationalIdentityNumber(anyString())).thenReturn(Optional.ofNullable(user));

        Optional<User> actual = userService.findByNationalIdentityNumber("295345");

        assertNotNull(actual);
        assertEquals(user, actual.get());

        verify(userRepository, times(1)).findByNationalIdentityNumber(anyString());
    }

    @Test
    public void testFindByNationalIdentityNumberWhenNotUserFound() {
        when(userRepository.findByNationalIdentityNumber(anyString())).thenReturn(Optional.empty());

        Optional<User> actual = userService.findByNationalIdentityNumber("f4adt5");

        assertNotNull(actual);
        assertFalse(actual.isPresent());

        verify(userRepository, times(1)).findByNationalIdentityNumber(anyString());
    }

}
