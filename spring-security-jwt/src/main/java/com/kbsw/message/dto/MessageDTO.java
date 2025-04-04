package com.kbsw.message.dto;

import java.time.LocalDateTime;

import com.kbsw.comment.entity.Comment;
import com.kbsw.message.entity.Message;

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
public class MessageDTO {
	private Long sender;
	private Long receiver;
	private String content;

	public static MessageDTO fromEntity(Long sender, Long receiver, String content){
		return MessageDTO.builder()
			.sender(sender)
			.receiver(receiver)
			.content(content)
			.build();
	}
}