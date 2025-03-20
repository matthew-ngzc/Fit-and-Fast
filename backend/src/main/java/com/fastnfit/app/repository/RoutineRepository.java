// RoutineRepository.java
package com.fastnfit.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fastnfit.app.model.Routine;
import com.fastnfit.app.model.User;

import java.util.List;

@Repository
public interface RoutineRepository extends JpaRepository<Routine, Long> {
    List<Routine> findByUser(User user);
}
