package com.example.didle.controller.api; // 패키지 확인

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
@RequestMapping("/api/users") // 기본 경로 유지
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // --- 기존 CRUD 엔드포인트 유지 (변경 없음) ---
    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
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

    // --- 로그인 엔드포인트 유지 (변경 없음) ---
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody User user, HttpServletRequest request) {
        // 주의: 실제로는 비밀번호 해싱 비교 로직 필요!
        User authenticatedUser = userService.authenticateUser(user.getUsername(), user.getPasswordHash());
        if (authenticatedUser != null) {
            HttpSession session = request.getSession(true); // 세션 생성 또는 기존 세션 반환
            session.setAttribute("userId", authenticatedUser.getId());
            session.setAttribute("username", authenticatedUser.getUsername());
            session.setAttribute("userType", authenticatedUser.getUserType()); // UserType enum 저장

            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("userId", authenticatedUser.getId());
            responseBody.put("username", authenticatedUser.getUsername());
            responseBody.put("userType", authenticatedUser.getUserType().name()); // Enum 이름을 문자열로
            return ResponseEntity.ok(responseBody);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid username or password"));
        }
    }

    // --- 로그아웃 엔드포인트 수정 (응답 변경) ---
    @PostMapping("/logout")
    public ResponseEntity<Void> logoutUser(HttpServletRequest request) { // 반환 타입 Void로 변경
        HttpSession session = request.getSession(false); // 기존 세션만 가져옴
        if (session != null) {
            session.invalidate(); // 세션 무효화
        }
        return ResponseEntity.noContent().build(); // 204 No Content 반환
    }

    // --- 현재 사용자 정보 엔드포인트 수정 (경로 및 반환 필드 조정) ---
    @GetMapping("/me") // 경로를 /me 로 변경
    public ResponseEntity<Map<String, Object>> getCurrentUser(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        String username = (String) session.getAttribute("username");
        User.UserType userType = (User.UserType) session.getAttribute("userType");

        if (userId == null || username == null || userType == null) {
            // 세션에 정보 없으면 비로그인 상태
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // 본문 없이 401 반환
        }

        Map<String, Object> responseBody = new HashMap<>();
        // JavaScript에서 필요한 필드 이름으로 매핑 ('role' 대신 'userType' 사용)
        responseBody.put("username", username);
        responseBody.put("role", userType.name()); // JavaScript의 'role' 필드에 UserType 이름 매핑

        // --- 필요하다면 다른 정보 추가 ---
        // responseBody.put("id", userId); // ID가 필요하다면 추가
        // User user = userService.getUserById(userId);
        // if (user != null) {
        //     responseBody.put("email", user.getEmail());
        //     responseBody.put("fullName", user.getFullName());
        // }

        return ResponseEntity.ok(responseBody);
    }
}
