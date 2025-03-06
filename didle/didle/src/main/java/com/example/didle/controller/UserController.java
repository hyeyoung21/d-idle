package com.example.didle.controller;

import com.example.didle.model.Business;
import com.example.didle.model.User;
import com.example.didle.service.BusinessService;
import com.example.didle.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final BusinessService businessService;

    public UserController(UserService userService, BusinessService businessService) {
        this.userService = userService;
        this.businessService = businessService;
    }

    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        System.out.println(user);
        return user != null ? ResponseEntity.ok(user) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        System.out.println(user);
        return userService.createUser(user);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {

        User updatedUser = userService.updateUser(user);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody User user, HttpServletRequest request) {
        User authenticatedUser = userService.authenticateUser(user.getUsername(), user.getPasswordHash());
        if (authenticatedUser != null) {
            HttpSession session = request.getSession();
            session.setAttribute("userId", authenticatedUser.getId());
            session.setAttribute("username", authenticatedUser.getUsername());
            session.setAttribute("userType", authenticatedUser.getUserType());

            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("userId", authenticatedUser.getId());
            responseBody.put("username", authenticatedUser.getUsername());
            responseBody.put("userType", authenticatedUser.getUserType());

            // BUSINESS 유저 타입인 경우 비즈니스 정보 추가
            if (User.UserType.BUSINESS.equals(authenticatedUser.getUserType())) {
                Business business = businessService.getBusinessByUserId(authenticatedUser.getId());
                if (business != null) {
                    session.setAttribute("businessId", business.getId());
                    session.setAttribute("businessName", business.getBusinessName());
                    responseBody.put("businessId", business.getId());
                    responseBody.put("businessName", business.getBusinessName());
                }
            }

            return ResponseEntity.ok(responseBody);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid username or password"));
        }
    }


    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return ResponseEntity.ok(new HashMap<String, String>() {{
            put("message", "Logged out successfully");
        }});
    }

}
