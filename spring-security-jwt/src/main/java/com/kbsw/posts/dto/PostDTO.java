package com.kbsw.posts.dto;

import com.kbsw.comment.dto.CommentDTO;
import com.kbsw.comment.entity.Comment;
import com.kbsw.posts.entity.Post;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostDTO {
    private Integer id;
    private Integer userId;
    private String name;
    private String title;
    private String content;
    private String fileName;        // 저장된 파일명 (UUID 포함)
    private String originFileName;  // 원본 파일명
    private Integer fileSize;       // 파일 크기
    private Integer readNum;        // 조회수
    private LocalDateTime wdate;    // 작성일
    private List<CommentDTO> comments;
    //엔티티 -> DTO변환
    public static PostDTO fromEntity(Post entity){
        return PostDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .title(entity.getTitle())
                .content(entity.getContent())
                .userId(entity.getUserId())
                .fileName(entity.getFileName())
                .originFileName(entity.getOriginFileName())
                .fileSize(entity.getFileSize())
                .readNum(entity.getReadNum())
                .wdate(entity.getWdate())
                .comments(entity.getComments().stream()
                .map(CommentDTO::fromEntity)
                .toList())
                .build();
    }
    //DTO -> 엔티티로 변환
    public Post toEntity() {
        return Post.builder()
                .id(id)
                .name(name)
                .title(title)
                .content(content)
                .userId(userId)
                .fileName(fileName)
                .originFileName(originFileName)
                .fileSize(fileSize)
                .readNum(readNum != null ? readNum : 0)
                .wdate(wdate)
                .build();
    }

}
