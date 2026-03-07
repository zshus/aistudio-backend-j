package com.example.backend_j.login.controller.response;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import java.util.List;
import com.example.backend_j.login.application.domain.User;

@Builder
@Getter
@AllArgsConstructor
public class UserResponse {
    private Long userId;
    private String username;
    private String email;

    public static UserResponse form(User user){
        return UserResponse.builder()
        .userId(user.getId())
        .username(user.getUsername())
        .email(user.getEmail())
        .build();
    }

    public static List<UserResponse> form(List<User> list){
        return list.stream()
                .map(UserResponse::form)
                .toList();
    }
    
}
