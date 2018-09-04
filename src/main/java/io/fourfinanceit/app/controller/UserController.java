package io.fourfinanceit.app.controller;

import io.fourfinanceit.app.exception.ResourceNotFoundException;
import io.fourfinanceit.app.exception.UserAlreadyExistsException;
import io.fourfinanceit.app.model.domain.User;
import io.fourfinanceit.app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping(
            value = "/{userId}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public User getUser(
            @PathVariable("userId") Long userId) {
        return userService.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
    }

    @RequestMapping(
            value = "",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity createUser(
            @Valid @RequestBody User newUser) {
        if (userService.findByNationalIdentityNumber(newUser.getNationalIdentityNumber()).isPresent()) {
            throw new UserAlreadyExistsException(newUser.getNationalIdentityNumber());
        }
        userService.saveUser(newUser);
        return ResponseEntity.ok(HttpStatus.OK);
    }

}
