package com.kbsw.user.controller;

import com.kbsw.spring_jpa.util.JwtUtil;
import com.kbsw.user.dto.LoginRequestDTO;
import com.kbsw.user.dto.LoginResponseDTO;
import com.kbsw.user.entity.MemberEntity;
import com.kbsw.user.repository.MemberRepository;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

//리액트와 로그인 연동 컨트롤러
//로그인 시 회원인증을 받으면 accessToken과 refreshToken을 발급한다
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class LoginApiController {
    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO request) {
        log.info("login요청 들어옴: "+request);
        Optional<MemberEntity> memberOpt = memberRepository.findByEmail(request.getEmail());

        if (memberOpt.isEmpty() || !passwordEncoder.matches(request.getPasswd(), memberOpt.get().getPasswd())) {
            return ResponseEntity.status(401).body("아이디 또는 비밀번호가 틀렸습니다.");
        }

        MemberEntity member = memberOpt.get();
        String accessToken = jwtUtil.generateAccessToken(member);
        String refreshToken = jwtUtil.generateRefreshToken(member);

        // refreshToken 저장
        member.setRefreshToken(refreshToken);
        memberRepository.save(member);

        // DTO를 이용해 응답 생성
        LoginResponseDTO response = new LoginResponseDTO(
                "success",
                "로그인 성공",
                Map.of("name", member.getName(),
                        "email",member.getEmail(),
                        "role",member.getRole(),
                        "id", member.getId()),// 회원 정보
                accessToken,
                refreshToken
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");

        if (refreshToken == null) {
            return ResponseEntity.status(401).body("refresh token이 없습니다.");
        }

        try {
            Claims claims = jwtUtil.validateToken(refreshToken);
            Optional<MemberEntity> memberOpt = memberRepository.findById(Long.parseLong(claims.get("id").toString()));

            if (memberOpt.isEmpty() || !refreshToken.equals(memberOpt.get().getRefreshToken())) {
                return ResponseEntity.status(403).body("인증되지 않은 회원입니다.");
            }

            MemberEntity member = memberOpt.get();
            String newAccessToken = jwtUtil.generateAccessToken(member);

            return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
        } catch (Exception e) {
            return ResponseEntity.status(403).body("유효하지 않은 Refresh Token입니다.");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@AuthenticationPrincipal MemberEntity userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).body("인증되지 않은 사용자입니다.");
        }

        // 로그인된 사용자의 이메일 가져오기
        String email = userDetails.getUsername();

        // DB에서 refreshToken 제거
        int updatedRows = memberRepository.updateRefreshToken(email, null);

        if (updatedRows > 0) {
            return ResponseEntity.ok(Map.of("result", "success", "message", "로그아웃 되었습니다."));
        } else {
            return ResponseEntity.badRequest().body(Map.of("message", "유효하지 않은 사용자입니다."));
        }
    }

    @GetMapping("/user")
    public ResponseEntity<?> getUserInfo(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("토큰이 없습니다.");
        }

        // 토큰에서 사용자 정보 추출
        String accessToken = authHeader.substring(7); // "Bearer " 제거
        Claims claims = jwtUtil.validateToken(accessToken);
        String email = claims.get("email", String.class);

        Optional<MemberEntity> memberOpt = memberRepository.findByEmail(email);
        if (memberOpt.isEmpty()) {
            return ResponseEntity.status(403).body("유효하지 않은 사용자입니다.");
        }

        MemberEntity member = memberOpt.get();

        return ResponseEntity.ok(Map.of(
            "id", member.getId(),
            "email", member.getEmail(),
            "name", member.getName(),
            "role", member.getRole()
        ));
    }


}
