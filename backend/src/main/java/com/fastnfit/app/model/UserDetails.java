// UserDetails.java
package com.fastnfit.app.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;

@Data
@Entity
@Table(name = "user_details")
public class UserDetails {
    
    @Id
    private Long userId;
    
    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;
    
    @Column(nullable = false)
    private String username;
    
    @Temporal(TemporalType.DATE)
    private Date dob;
    
    private Integer height;
    
    private Integer weight;
    
    private Boolean isPregnant;
    
    private String workoutGoal;
    
    private Integer workoutDays;
}
