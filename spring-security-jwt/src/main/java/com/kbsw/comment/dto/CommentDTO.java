package com.kbsw.comment.dto;

import java.time.LocalDateTime;

import org.springframework.cglib.core.Local;

import com.kbsw.comment.entity.Comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentDTO {
	private Integer id;
	private Integer postId;
	private Long userId;
	private String content;
	private LocalDateTime wdate;
	private String fileName;
	private String userName;

	public static CommentDTO fromEntity(Comment comment){
		return CommentDTO.builder()
			.id(comment.getId())
			.postId(comment.getPost().getId())
			.userId(comment.getUser().getId())
			.wdate(comment.getWdate())
			.content(comment.getContent())
			.fileName(comment.getUser().getFileName())
			.userName(comment.getUser().getName())
			.build();
	}
}
