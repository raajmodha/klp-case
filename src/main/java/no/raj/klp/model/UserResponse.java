package no.raj.klp.model;

public record UserResponse(Integer id, String email, String type) {

    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getEmail(), user.getType().name());
    }
}
