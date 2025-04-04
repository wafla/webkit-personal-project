package com.kbsw.posts.entity;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;

import org.antlr.v4.runtime.misc.NotNull;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.kbsw.comment.entity.Comment;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="friends_posts")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Integer userId;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(length = 2000)
    private String content;

    @Column(length = 100)
    private String fileName; // 물리적 파일명

    @Column(length = 100)
    private String originFileName; // 원본 파일명

    private Integer fileSize; // 파일 크기

    @Column(nullable = false)
    @Builder.Default
    private Integer readNum = 0; // 기본값 0

    @CreationTimestamp
    private LocalDateTime wdate; // 생성일 자동 설정

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("wdate DESC")
    @JsonManagedReference
    private List<Comment> comments = new ArrayList<>();
}
