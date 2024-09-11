package com.crusaders.eprescriptionserver.controller;

import com.crusaders.eprescriptionserver.entity.User;
import com.crusaders.eprescriptionserver.service.UserService;
import com.crusaders.eprescriptionserver.utilis.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody User user) {
        try {
            User existingUser = userService.findByEmail(user.getEmail());
            if (existingUser != null) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(Map.of("message", "User already exists, you can login", "success", false));
            }

            user.setPassword(user.getPassword().trim());
            userService.registerUser(user);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message", "Signup successful", "success", true));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Internal server error", "success", false));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        try {
            User existingUser = userService.findByEmail(user.getEmail());
            System.out.println("Existing user: " + existingUser); // Log the existing user

            if (existingUser == null) {
                System.out.println("User not found"); // Log if user is not found
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("message", "Auth failed email or password is wrong", "success", false));
            }

            boolean passwordMatches = bCryptPasswordEncoder.matches(user.getPassword(), existingUser.getPassword());
            System.out.println("Password matches: " + passwordMatches); // Log if password matches

            if (!passwordMatches) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("message", "Auth failed email or password is wrong", "success", false));
            }

            String jwtToken = jwtUtil.generateToken(existingUser.getEmail());

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Login Success");
            response.put("success", true);
            response.put("jwtToken", jwtToken);
            response.put("email", existingUser.getEmail());
            response.put("name", existingUser.getFullName());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace(); // Log the full stack trace
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Internal server error", "success", false));
        }
    }
}
