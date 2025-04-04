package com.kbsw.comment.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kbsw.comment.dto.CommentRequestDto;
import com.kbsw.comment.entity.Comment;
import com.kbsw.comment.service.CommentService;
import com.kbsw.user.dto.ResponseDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/comment")
@RequiredArgsConstructor
public class CommentController {
	private final CommentService commentService;

	@PostMapping
	public ResponseEntity<Comment> createComment(@RequestBody CommentRequestDto requestDto) {
		Comment savedComment = commentService.createComment(
			requestDto.postId(),
			requestDto.userId(),
			requestDto.content()
		);
		return ResponseEntity.ok(savedComment);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<ResponseDTO> deleteComment(@PathVariable("id") Integer id){
		log.info("Delete Comment ID: {}", id);
		commentService.deleteComment(id);
		return ResponseEntity.ok(new ResponseDTO("success", "삭제 완료", null));
	}
}
