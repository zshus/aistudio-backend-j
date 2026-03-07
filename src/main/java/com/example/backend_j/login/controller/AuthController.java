package com.example.backend_j.login.controller;

import com.example.backend_j.login.application.UserService;
import com.example.backend_j.login.controller.request.LoginRequest;
import com.example.backend_j.login.controller.request.RegisterRequest;
import com.example.backend_j.login.controller.response.LoginResponse;
import com.example.backend_j.login.controller.response.UserResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginResponse response = userService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody RegisterRequest request) {
        userService.register(request);
        return ResponseEntity.ok(Map.of("message", "회원가입이 완료되었습니다."));
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getAllList() {
        List<UserResponse> list=userService.getAllList();
        return ResponseEntity.ok(list);
    }
    

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleBadRequest(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
    }
}
