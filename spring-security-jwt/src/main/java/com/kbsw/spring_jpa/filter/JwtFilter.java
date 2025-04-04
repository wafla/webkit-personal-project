package com.kbsw.spring_jpa.filter;

import com.kbsw.spring_jpa.util.JwtUtil;
import com.kbsw.user.repository.MemberRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;

@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {//Spring Security에서 요청당 한 번만 실행되는 필터를 만들 때 사용
    //같은 요청이 여러 개의 필터 체인을 거치더라도, OncePerRequestFilter를 상속한 필터는 딱 한 번만 실행된다. jwt필터로 많이 사용됨
    private final JwtUtil jwtUtil;
    private final MemberRepository memberRepository; // 추가 (DB에서 refreshToken 확인)

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String token = request.getHeader("Authorization");

        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);

            try {
                var claims = jwtUtil.validateToken(token);
                String email = claims.get("email", String.class);
                String role = claims.get("role", String.class);

                // 1. DB에서 refreshToken 확인
                String refreshToken = memberRepository.findRefreshTokenByEmail(email);
                if (refreshToken == null) {
                    // 로그아웃된 사용자 -> 401 Unauthorized 응답
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("로그아웃된 사용자입니다.");
                    return;
                }

                // 2. 정상 사용자 -> SecurityContext에 인증 정보 저장
                Set<SimpleGrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority("ROLE_" + role));
                var auth = new UsernamePasswordAuthenticationToken(
                        new User(email, "", authorities), null, authorities
                );
                SecurityContextHolder.getContext().setAuthentication(auth);//세션에 인증객체 저장

            } catch (Exception e) {
                SecurityContextHolder.clearContext();
            }
        }

        chain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        String method = request.getMethod();
        return (path.equals("/api/login") || path.equals("/signup") ||
                (path.equals("/api/users") && "POST".equalsIgnoreCase(method)));//회원가입
    }
}
