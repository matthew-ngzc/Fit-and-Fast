// HistoryRepository.java
package com.fastnfit.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fastnfit.app.model.History;
import com.fastnfit.app.model.User;

import java.util.Date;
import java.util.List;

@Repository
public interface HistoryRepository extends JpaRepository<History, Long> {
    List<History> findByUser(User user);
    List<History> findByUserAndRoutineDateBetween(User user, Date startDate, Date endDate);
}
