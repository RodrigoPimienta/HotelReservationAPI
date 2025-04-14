package com.revature.repos;

import com.revature.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserDAO extends JpaRepository<User, Integer> {

    Optional<User> findUserByEmail(String email);
}
