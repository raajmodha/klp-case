package no.raj.klp.service;

import no.raj.klp.exception.UserNotFoundException;
import no.raj.klp.model.User;
import no.raj.klp.model.UserRequest;
import no.raj.klp.model.UserResponse;
import no.raj.klp.model.UserType;
import no.raj.klp.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

@Service
public class UserService {

    private static final Logger logger = LogManager.getLogger(UserService.class);
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponse createUser(UserRequest request) {
        User saved = userRepository.save(new User(request.email(), request.type()));
        logger.info("User created - id: {}, type: {}", saved.getId(), saved.getType());
        return UserResponse.from(saved);
    }

    public UserResponse getUserById(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
        return UserResponse.from(user);
    }

    public List<UserResponse> getUsers(UserType typeFilter) {
        List<User> users = (typeFilter != null)
                ? userRepository.findByType(typeFilter)
                : userRepository.findAll();
        return users.stream().map(UserResponse::from).toList();
    }
}
