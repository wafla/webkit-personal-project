package com.kbsw.message.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MessageUserDTO {
	private Long id;
	private String name;
	private String fileName;
	private String lastMessage;
	private String lastMessageTime;
}
