package com.example.didle.controller.api;

import com.example.didle.model.vo.User;
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

    public UserController(UserService userService) {
        this.userService = userService;
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

    @GetMapping("/current")
    public ResponseEntity<Map<String, Object>> getCurrentUser(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        String username = (String) session.getAttribute("username");
        User.UserType userType = (User.UserType) session.getAttribute("userType");

        if (userId == null || username == null || userType == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "User not logged in"));
        }

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("id", userId);
        responseBody.put("username", username);
        responseBody.put("userType", userType);

        // 사용자 추가 정보 가져오기
        User user = userService.getUserById(userId);
        if (user != null) {
            responseBody.put("email", user.getEmail());
            responseBody.put("fullName", user.getFullName());
            responseBody.put("phone", user.getPhone());
            responseBody.put("address", user.getAddress());
        }

        return ResponseEntity.ok(responseBody);
    }
}
