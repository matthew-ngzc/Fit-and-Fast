// RecommendationDTO.java
package com.fastnfit.app.dto;

public class RecommendationDTO {
    private String workoutId;
    private String title;
    private String description;
    private String recommendation;
    
    public RecommendationDTO() {}
    
    public RecommendationDTO(String workoutId, String title, String description, String recommendation) {
        this.workoutId = workoutId;
        this.title = title;
        this.description = description;
        this.recommendation = recommendation;
    }
    
    public String getWorkoutId() {
        return workoutId;
    }
    
    public void setWorkoutId(String workoutId) {
        this.workoutId = workoutId;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getRecommendation() {
        return recommendation;
    }
    
    public void setRecommendation(String recommendation) {
        this.recommendation = recommendation;
    }
}
