package no.raj.klp.repository;

import no.raj.klp.model.User;
import no.raj.klp.model.UserType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    List<User> findByType(UserType type);
}
