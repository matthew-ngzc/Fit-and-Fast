package com.fastnfit.app.UnitTests;

import com.fastnfit.app.config.AwsConfig;
import com.fastnfit.app.config.JwtConfig;
import com.fastnfit.app.controller.ChatbotController;
import com.fastnfit.app.dto.UserDetailsDTO;
import com.fastnfit.app.enums.FitnessLevel;
import com.fastnfit.app.service.ChatbotService;
import com.fastnfit.app.service.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.Date;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import org.springframework.test.context.ActiveProfiles;


@WebMvcTest(ChatbotController.class)
@ImportAutoConfiguration(exclude = {
    DataSourceAutoConfiguration.class,
    HibernateJpaAutoConfiguration.class
})
@ActiveProfiles("dev")
public class ChatbotControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private ChatbotService chatbotService;

    @MockBean
    private JwtConfig jwtConfig;

    @MockBean
    private AwsConfig awsConfig;

    private UserDetailsDTO mockUserDetails;

    @BeforeEach
    void setUp() {
        mockUserDetails = new UserDetailsDTO();
        mockUserDetails.setUserId(1L);
        mockUserDetails.setUsername("TestUser");
        mockUserDetails.setDob(new Date(946684800000L)); // 2000-01-01
        mockUserDetails.setHeight(160.0);
        mockUserDetails.setWeight(55.0);
        mockUserDetails.setFitnessLevel(FitnessLevel.BEGINNER);
        mockUserDetails.setWorkoutGoal("FAT_LOSS");
        mockUserDetails.setWorkoutType("CARDIO");
        mockUserDetails.setMenstrualCramps(true);
        mockUserDetails.setCycleBasedRecommendations(true);

        Mockito.when(jwtConfig.getExpiration()).thenReturn(3600000L); // mock expiration
    }

    @WithMockUser(username = "testuser", roles = {"USER"})
    @Test
    void testChatbot_ReturnsExpectedMessage() throws Exception {
        String expectedReply = "**Here is your modified routine**\n- Jumping Jacks\n- Squats";

        Mockito.when(userService.getUserDetails(1L)).thenReturn(mockUserDetails);
        Mockito.when(chatbotService.getResponse(anyString(), any(), anyMap()))
                .thenReturn(expectedReply);

        String requestJson = """
        {
            "message": "Make it easier",
            "currentWorkout": {
                "format": "40s work, 20s rest",
                "exercises": ["Jumping Jacks", "Push Ups", "Squats"]
            }
        }
        """;

        MvcResult result = mockMvc.perform(post("/api/chatbot/1")
                .with(csrf()) // ✅ Disable CSRF blocking
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(expectedReply))
                .andReturn();

        System.out.println("💬 Chatbot replied: " + result.getResponse().getContentAsString());
    }
}
