package com.example.didle.model.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CurrentUserInfoDto {
    private final String username;
    private final String role; // "CUSTOMER", "ADMIN"
}
