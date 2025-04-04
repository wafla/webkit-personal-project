package com.kbsw.spring_jpa;

import com.kbsw.spring_jpa.filter.JwtFilter;
import com.kbsw.spring_jpa.util.JwtUtil;
import com.kbsw.user.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//JWT이용하는 WebSecurityConfig
@Configuration
@EnableWebSecurity
public class WebSecurityAndJwtConfig {

    @Autowired
    private UserDetailsService userDetailService;

    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private MemberRepository memberRepository;

    private static final String[] WHITE_LIST
            ={ "/", "/api/auth/login", "/api/users","/api/users/*","/api/users/check-name", "/api/users/check-email",
            "/signup", "/public/**","/api/posts","/api/posts/*", "/api/comment", "/api/comment/*",
        "/api/messages", "/api/messages/send", "/api/messages/chat-users", "/api/auth/user", "/uploads/*"};

    @Bean
    public WebSecurityCustomizer configure(){
        return (web->web.ignoring()
                .requestMatchers("/static/**")
        );
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**") // API 경로 지정
                        .allowedOrigins("http://localhost:5000") // React 프론트엔드 주소
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 허용할 HTTP 메서드
                        .allowCredentials(true);
            }
        };
    }

    @Bean
    public JwtFilter jwtFilter() {
        return new JwtFilter(jwtUtil, memberRepository);
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception{
        http
                .cors(cors -> cors.disable()) // CORS 설정 필요하면 따로 추가
                .csrf(csrf -> csrf.disable()) // REST API에서는 CSRF 보호 필요 없음
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(WHITE_LIST).permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/users").hasRole("ADMIN")
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/auth/**").hasAnyRole("USER","ADMIN")
                        .anyRequest().authenticated()
                )
                //.formLogin() //==>리액트에서 로그인 폼 제공하므로 있으면 안된다
                .logout(logout -> logout
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)
                )
                .exceptionHandling(exception ->
                        exception.accessDeniedPage("/accessDenied")
                )
                // JWT 필터 추가 (UsernamePasswordAuthenticationFilter 앞에 배치)
                .addFilterBefore(jwtFilter(),  UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            HttpSecurity http, BCryptPasswordEncoder passwordEncoder)
            throws Exception {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(authProvider);
    }
}