package com.kbsw.spring_jpa.util;



import com.kbsw.user.entity.MemberEntity;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.Map;

@Component
public class JwtUtil {
    private final Key key; //HMAC SHA 알고리즘 비밀키(secretKey)가 저장되는 변수(서명과 검증에 사용됨)
    private final long accessTokenExpiration;
    private final long refreshTokenExpiration;

    //application-dev.yml에서 jwt.secret등의 키에 해당하는 값을 가져와 @Value로 주입함
    public JwtUtil(@Value("${jwt.secret}") String secret,
                   @Value("${jwt.access-token-expiration}") long accessTokenExpiration,
                   @Value("${jwt.refresh-token-expiration}") long refreshTokenExpiration) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());//HMAC SHA 알고리즘에 맞는 Key 객체 생성
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    // JWT 생성
    public String generateToken(Map<String, Object> claims, long expiration) {
        return Jwts.builder()
                .setClaims(claims) //payload가 담김(사용자 정보)
                .setIssuedAt(new Date())//발급일
                .setExpiration(new Date(System.currentTimeMillis() + expiration))//만료 시간
                .signWith(key, SignatureAlgorithm.HS256)//서명
                .compact();
    }



    public String generateAccessToken(MemberEntity member) {
        return generateToken(Map.of("id", member.getId(),
                "email", member.getEmail(),
                "role",member.getRole()), accessTokenExpiration);
    }

    public String generateRefreshToken(MemberEntity member) {
        return generateToken(Map.of("id", member.getId(),
                "email", member.getEmail(),
                "role",member.getRole()), refreshTokenExpiration);
    }

    // JWT 검증 : key를 이용해 토큰이 변조되지 않았는지 확인하고, 만료시간을 체크하며, 
    // jwt의 구조(헤더,페이로드,서명)가 올바른지 체크한다
    public Claims validateToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)//서명 검증을 위하 키 설정
                .build()
                .parseClaimsJws(token) //토큰 검증 및 파싱
                .getBody();//jwt의 payload(Claims) 추출
    }
}
