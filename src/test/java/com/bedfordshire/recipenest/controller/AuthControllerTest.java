package com.bedfordshire.recipenest.controller;

import com.bedfordshire.recipenest.dto.auth.AuthResponse;
import com.bedfordshire.recipenest.entity.UserRole;
import com.bedfordshire.recipenest.service.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(AuthController.class)
public class AuthControllerTest {


    // This is a spring test tool for testing controllers
    // Without starting the sever
    /**
     * Lets you send a fake Get || Post request
     * send JSON in the body
     * Check the HTTP status
     * Check the JSON Response
     * Instead of using postman
     */
    @Autowired
    private MockMvc mockMvc;

    /**
     * MockitoBean Tells Spring
     * When building this test use a fake Mockito version of this
     * dependency instead of the real one
     * As i want to test the controller not business logic
     */
    @MockitoBean
    private AuthService authService;

    @Test
    @DisplayName("Register with valid request returns 200 OK")
    void register_validRequest_returnsOk() throws Exception {
        // Service method returns void so check for 200
        doNothing().when(authService).register(any());

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                        "firstname" : "Obinna",
                        "lastname" : "Uche",
                        "email" : "obinna@example.com",
                        "password" : "password123",
                        "role" : "PUBLIC"
                        }
                        """))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Login with valid request returns auth response")
    void login_validRequest_returnsAuthResponse() throws Exception {
        AuthResponse response = new AuthResponse(
                "access-token-123",
                "refresh-token-456",
                "Bearer",
                3600,
                "obinna@example.com",
                "USER"
        );

        when(authService.login(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "obinna@example.com",
                                  "password": "password123"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access-token-123"))
                .andExpect(jsonPath("$.refreshToken").value("refresh-token-456"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.expiresIn").value(3600))
                .andExpect(jsonPath("$.email").value("obinna@example.com"))
                .andExpect(jsonPath("$.role").value("USER"));
    }

    @Test
    @DisplayName("Login with blank password returns 400 Bad Request")
    void login_blankPassword_returnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "obinna@example.com",
                                  "password": ""
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Verify email with token returns 200 OK")
    void verifyEmail_validToken_returnsOk() throws Exception {
        doNothing().when(authService).verifyEmail("sample-token");

        mockMvc.perform(get("/api/v1/auth/verify-email")
                        .param("token", "sample-token"))
                .andExpect(status().isOk());
    }


}
