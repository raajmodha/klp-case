package no.raj.klp.controller;

import jakarta.validation.Valid;
import no.raj.klp.model.UserRequest;
import no.raj.klp.model.UserResponse;
import no.raj.klp.model.UserType;
import no.raj.klp.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Integer id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> getUsers(@RequestParam(name = "type-filter", required = false) UserType typeFilter) {
        return ResponseEntity.ok(userService.getUsers(typeFilter));
    }
}
