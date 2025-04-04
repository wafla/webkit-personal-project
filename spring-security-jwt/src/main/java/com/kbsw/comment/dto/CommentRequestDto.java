package com.kbsw.comment.dto;

public record CommentRequestDto(
	Integer postId,
	Long userId,
	String content
) {

}
