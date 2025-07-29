package com.fastnfit.app.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class ChatHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    private String role;     // "user" or "assistant"

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    private LocalDateTime timestamp;
}
