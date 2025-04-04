package com.kbsw.message.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class MessageRequestDTO {
	private Long senderId;
	private Long receiverId;
	private String content;
}
