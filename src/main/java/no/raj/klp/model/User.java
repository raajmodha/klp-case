package no.raj.klp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "Email must not be blank")
    @Email(message = "Must be a valid email address")
    @Column(nullable = false, unique = true)
    private String email;

    @NotNull(message = "Type must not be null")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserType type;

    public User() {}

    public User(String email, UserType type) {
        this.email = email;
        this.type = type;
    }

    public Integer getId() { return id; }

    public void setId(Integer id) { this.id = id; }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    public UserType getType() { return type; }

    public void setType(UserType type) { this.type = type; }
}
