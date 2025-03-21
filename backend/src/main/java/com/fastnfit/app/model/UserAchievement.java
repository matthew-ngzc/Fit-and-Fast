package com.fastnfit.app.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "user_achievements")
public class UserAchievement {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    @ManyToOne
    @JoinColumn(name = "achievement_id")
    private Achievement achievement;
    
    @Column(nullable = false)
    private boolean completed = false;
}