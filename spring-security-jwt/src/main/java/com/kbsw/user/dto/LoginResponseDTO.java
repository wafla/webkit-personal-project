package com.kbsw.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public class LoginResponseDTO {
    private String result;
    private String message;
    private Map data; // 회원정보(id,name,email,role)
    private String accessToken;
    private String refreshToken;
}
