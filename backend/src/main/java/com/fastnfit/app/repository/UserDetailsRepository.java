// UserDetailsRepository.java
package com.fastnfit.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fastnfit.app.model.UserDetails;
import com.fastnfit.app.model.User;

import java.util.Optional;

@Repository
public interface UserDetailsRepository extends JpaRepository<UserDetails, Long> {
    Optional<UserDetails> findByUser(User user);
    Optional<UserDetails> findByUserUserId(Long userId);
    Optional<UserDetails> findByUsername(String username);
}
