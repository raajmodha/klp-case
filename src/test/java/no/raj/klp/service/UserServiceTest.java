package no.raj.klp.service;

import no.raj.klp.exception.UserNotFoundException;
import no.raj.klp.model.User;
import no.raj.klp.model.UserRequest;
import no.raj.klp.model.UserResponse;
import no.raj.klp.model.UserType;
import no.raj.klp.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void createdUserResponseReflectsEmailAndTypeFromRequest() {
        User saved = userWithId(1, "jan@klp.no", UserType.USER);
        when(userRepository.save(any(User.class))).thenReturn(saved);

        UserResponse response = userService.createUser(requestFor("jan@klp.no", UserType.USER));

        assertThat(response.email()).isEqualTo("jan@klp.no");
        assertThat(response.type()).isEqualTo("USER");
    }

    @Test
    void createdUserResponseContainsIdAssignedByRepository() {
        User saved = userWithId(42, "jan@klp.no", UserType.USER);
        when(userRepository.save(any(User.class))).thenReturn(saved);

        UserResponse response = userService.createUser(requestFor("jan@klp.no", UserType.USER));

        assertThat(response.id()).isEqualTo(42);
    }

    @Test
    void createUserPersistsCorrectEmailAndType() {
        User saved = userWithId(1, "jan@klp.no", UserType.ADMIN);
        when(userRepository.save(any(User.class))).thenReturn(saved);

        userService.createUser(requestFor("jan@klp.no", UserType.ADMIN));

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertThat(captor.getValue().getEmail()).isEqualTo("jan@klp.no");
        assertThat(captor.getValue().getType()).isEqualTo(UserType.ADMIN);
    }

    @Test
    void getUserByIdReturnsCorrectUser() {
        User stored = userWithId(5, "kari@klp.no", UserType.USER);
        when(userRepository.findById(5)).thenReturn(Optional.of(stored));

        UserResponse response = userService.getUserById(5);

        assertThat(response.id()).isEqualTo(5);
        assertThat(response.email()).isEqualTo("kari@klp.no");
        assertThat(response.type()).isEqualTo("USER");
    }

    @Test
    void getUserByIdThrowsWhenUserDoesNotExist() {
        when(userRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserById(99))
            .isInstanceOf(UserNotFoundException.class)
            .hasMessageContaining("99");
    }

    @Test
    void getUsersReturnsAllUsersWhenNoFilterGiven() {
        List<User> all = List.of(
            userWithId(1, "a@klp.no", UserType.USER),
            userWithId(2, "b@klp.no", UserType.ADMIN)
        );
        when(userRepository.findAll()).thenReturn(all);

        List<UserResponse> result = userService.getUsers(null);

        assertThat(result).hasSize(2);
        assertThat(result).extracting(UserResponse::email)
            .containsExactly("a@klp.no", "b@klp.no");
    }

    @Test
    void getUsersReturnsEmptyListWhenNoUsersExist() {
        when(userRepository.findAll()).thenReturn(List.of());

        List<UserResponse> result = userService.getUsers(null);

        assertThat(result).isEmpty();
    }

    @Test
    void getUsersFiltersCorrectlyByType() {
        List<User> admins = List.of(userWithId(3, "admin@klp.no", UserType.ADMIN));
        when(userRepository.findByType(UserType.ADMIN)).thenReturn(admins);

        List<UserResponse> result = userService.getUsers(UserType.ADMIN);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).type()).isEqualTo("ADMIN");
        verify(userRepository).findByType(UserType.ADMIN);
    }

    @Test
    void getUsersReturnsEmptyListWhenNoUsersMatchFilter() {
        when(userRepository.findByType(UserType.ADMIN)).thenReturn(List.of());

        List<UserResponse> result = userService.getUsers(UserType.ADMIN);

        assertThat(result).isEmpty();
    }

    @Test
    void getUsersWithNullFilterCallsFindAllNotFindByType() {
        when(userRepository.findAll()).thenReturn(List.of());

        userService.getUsers(null);

        verify(userRepository).findAll();
    }

    private static User userWithId(int id, String email, UserType type) {
        User user = new User(email, type);
        user.setId(id);
        return user;
    }

    private static UserRequest requestFor(String email, UserType type) {
        return new UserRequest(email, type);
    }
}

