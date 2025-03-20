// History.java
package com.fastnfit.app.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;
import java.util.List;

@Data
@Entity
@Table(name = "history")
public class History {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long historyId;
    
    @Temporal(TemporalType.DATE)
    private Date routineDate;
    
    @Temporal(TemporalType.TIME)
    private Date routineTime;
    
    private String name;
    
    @ManyToMany
    @JoinTable(
        name = "history_workout_list",
        joinColumns = @JoinColumn(name = "history_id"),
        inverseJoinColumns = @JoinColumn(name = "workout_id")
    )
    private List<Workout> workoutList;
    
    @ManyToMany
    @JoinTable(
        name = "history_workout_did",
        joinColumns = @JoinColumn(name = "history_id"),
        inverseJoinColumns = @JoinColumn(name = "workout_id")
    )
    private List<Workout> workoutDid;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
