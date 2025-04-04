package com.kbsw.comment.service;


import org.springframework.stereotype.Service;

import com.kbsw.comment.entity.Comment;
import com.kbsw.comment.repository.CommentRepository;
import com.kbsw.posts.entity.Post;
import com.kbsw.posts.repository.PostRepository;
import com.kbsw.user.entity.MemberEntity;
import com.kbsw.user.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

	private final CommentRepository commentRepository;
	private final PostRepository postRepository;
	private final MemberRepository memberRepository;

	@Transactional
	public Comment createComment(Integer postId, Long userId, String commentText){
		Post post = postRepository.findById(postId)
			.orElseThrow(()->new RuntimeException("게시글을 찾을 수 없습니다."));

		MemberEntity user = memberRepository.findById(userId)
			.orElseThrow(()->new RuntimeException("유저를 찾을 수 없습니다"));

		Comment comment = new Comment();
		comment.setPost(post);
		comment.setUser(user);
		comment.setContent(commentText);

		return commentRepository.save(comment);
	}

	public void deleteComment(Integer id) {
		commentRepository.deleteById(id);
	}
}
