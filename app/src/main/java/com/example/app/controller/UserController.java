package com.example.app.controller;

import com.example.app.exceptions.UserDataException;
import com.example.app.model.User;
import com.example.app.service.DefaultEntityService;
import com.example.app.service.impl.UserDetailService;
import com.example.app.utill.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("user")
public class UserController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserDetailService userDetailService;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    DefaultEntityService<User> userService;

    @GetMapping
    public String welcome(){
        return "Welcome";
    }

    @PostMapping("/add")
    public ResponseEntity<?> addUser(@RequestBody User user){
        if(user.getUsername()==null || user.getPassword()==null){
            throw new UserDataException();
        }
        userService.add(user);
        return new ResponseEntity<>("User successfully created!",HttpStatus.CREATED);
    }


    @PostMapping("/authenticate")
    public ResponseEntity<?> createAuthToken(@RequestBody User user) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
        UserDetails userDetail = userDetailService.loadUserByUsername(user.getUsername());
        return new ResponseEntity<>(jwtTokenUtil.generateToken(userDetail),HttpStatus.CREATED);
    }

}
