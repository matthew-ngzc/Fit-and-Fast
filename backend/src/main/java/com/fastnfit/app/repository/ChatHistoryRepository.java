package com.fastnfit.app.repository;

import com.fastnfit.app.model.ChatHistory;
import com.fastnfit.app.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;




import java.util.List;

public interface ChatHistoryRepository extends JpaRepository<ChatHistory, Long> {
    List<ChatHistory> findByUserOrderByTimestampAsc(User user);
    ChatHistory findTopByUserAndRoleOrderByTimestampDesc(User user, String role);
    List<ChatHistory> findByUserOrderByTimestampDesc(User user, Pageable pageable);
    void deleteAllByUser(User user);
}
