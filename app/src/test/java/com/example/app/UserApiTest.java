package com.example.app;

import com.example.app.exceptions.UserDataException;
import com.example.app.model.User;
import com.example.app.repository.UserRepository;
import com.example.app.service.impl.UserService;
import com.example.app.utill.JwtTokenUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class UserApiTest extends ApplicationTests {

    @Autowired
    UserService userService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    JwtTokenUtil jwtTokenUtil;


    @Test
    void postUserAndGetSameUser() throws Exception {
        User user = new User();
        user.setPassword("444");
        user.setUsername("testUser" + new Random().nextInt(100));
        mvc.perform(post("/user/add")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isCreated())
                .andExpect(result -> assertThat(result
                        .getResponse()
                        .getContentAsString())
                        .isEqualTo("User successfully created!"));
        User entityUser = userRepository.findUserByUsername(user.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Error"));
        Assertions.assertTrue(passwordEncoder.matches(user.getPassword(), entityUser.getPassword()));
    }

    @Test
    void failPostUserWithoutUsernamePass() throws Exception {
        User user = new User();
        mvc.perform(post("/user/add")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> Assertions.assertInstanceOf(UserDataException.class, result.getResolvedException()));
    }

    @Test
    void authenticateUser() throws Exception {
        User user = new User();
        user.setPassword("444");
        user.setUsername("testUser");
        User entityUser = userRepository.findUserByUsername("testUser")
                .orElseThrow(() -> new UsernameNotFoundException("Error"));
        if(passwordEncoder.matches(user.getPassword(),entityUser.getPassword())){
            entityUser.setPassword(user.getPassword());
        }
        MvcResult mvcResult = mvc.perform(post("/user/authenticate")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(entityUser)))
                        .andExpect(status().isCreated())
                        .andReturn();
        String token = mvcResult.getResponse().getContentAsString();
        Assertions.assertNotNull(token);
        assertThat(user.getUsername()).isEqualTo(jwtTokenUtil.getUsername(token));
    }

    @Test
    void failAuthenticationWithBadCredentials() throws Exception {
        User entityUser = userRepository.findUserByUsername("dhd")
                .orElseThrow(() -> new UsernameNotFoundException("Error"));
        entityUser.setPassword("wrong_password");
        MvcResult mvcResult = mvc.perform(post("/user/authenticate")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(entityUser)))
                .andExpect(status().isUnauthorized())
                .andExpect(result -> Assertions.assertInstanceOf(BadCredentialsException.class, result.getResolvedException()))
                .andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        Assertions.assertNotNull(response);
        assertThat(response).isEqualTo("Bad Credentials!");
    }

}
