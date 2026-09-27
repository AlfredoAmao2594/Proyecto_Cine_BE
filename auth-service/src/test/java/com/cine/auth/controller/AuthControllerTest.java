package com.cine.auth.controller;

import com.cine.auth.config.SecurityConfig;
import com.cine.auth.dto.AuthResponse;
import com.cine.auth.security.JwtAuthenticationEntryPoint;
import com.cine.auth.security.JwtUtil;
import com.cine.auth.service.AuthService;
import com.cine.auth.support.TokenDePrueba;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import({SecurityConfig.class, JwtUtil.class, JwtAuthenticationEntryPoint.class})
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Test
    void guest_esPublico_respondeToken() throws Exception {
        when(authService.loginInvitado()).thenReturn(AuthResponse.builder()
                .token("eyJ...").tokenType("Bearer").name("Invitado").role("GUEST").build());

        mockMvc.perform(post("/api/auth/guest"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"))
                .andExpect(jsonPath("$.data.role").value("GUEST"));
    }

    @Test
    void me_sinToken_responde401() throws Exception {
        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void me_conToken_devuelveLosDatosDelToken() throws Exception {
        mockMvc.perform(get("/api/auth/me").header("Authorization", "Bearer " + TokenDePrueba.invitado()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.sub").value("guest-123"))
                .andExpect(jsonPath("$.data.name").value("Invitado"))
                .andExpect(jsonPath("$.data.role").value("GUEST"));
    }
}
