package com.kbsw.user.entity;

import jakarta.persistence.*;
import lombok.*;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.kbsw.comment.entity.Comment;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.kbsw.user.persistence.entity.Gender;

@Entity
@Table(name="user")
@NoArgsConstructor(access= AccessLevel.PROTECTED)
@Getter
@Setter
@Builder
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class MemberEntity implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="M_KEY")
    private Long id;

    @Column(name="M_NICKNAME", nullable = false)
    private String name;

    @Column(name="M_EMAIL", nullable = false)
    private String email;

    @Column(name="M_PASSWD", nullable = false)
    private String passwd;

    @Column(name="M_SCHOOL")
    private String school;

    // @Column(name="M_PROFILE_IMAGE")
    // private String profileImage;

    @Column(name="M_FILE_NAME")
    private String fileName;

    @Column(name="M_ORIGINAL_FILE_NAME")
    private String originalFileName;

    @Column(name="M_FILE_SIZE")
    private Integer fileSize;

    @Column(name="M_ABOUT_ME", length=1024)
    private String aboutMe;

    @Column(name="M_BIRTHDAY")
    private LocalDate birthDay;

    @Column(name="M_COUNTRY")
    private String country;

    @Enumerated(EnumType.STRING)
    @Column(name="M_GENDER")
    private Gender gender;

    @Column(name="M_HOBBY")
    private String hobby;

    @Column(name="M_ROLE")
    private String role;

    @Column(name="M_REFRESH_TOKEN")
    private String refreshToken;

    @CreatedDate
    @Column(name="M_INDATE")
    private LocalDateTime indate;

    @Column(name="M_LAST_LOGIN_AT")
    private LocalDateTime lastLoginAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Comment> comments;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities(){
        List arr= List.of(new SimpleGrantedAuthority("ROLE_"+this.role));
        return arr;
    }

    @Override
    public String getPassword() {return this.passwd;}

    @Override
    public String getUsername() {return this.email;}

    @Override
    public boolean isAccountNonExpired(){return true;}

    @Override
    public boolean isAccountNonLocked(){return true;}

    @Override
    public boolean isCredentialsNonExpired(){return true;}

    @Override
    public boolean isEnabled() {return true;}
}

