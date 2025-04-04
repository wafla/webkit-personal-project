package com.kbsw.message.controller;

import java.util.List;

import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kbsw.message.dto.MessageRequestDTO;
import com.kbsw.message.dto.MessageUserDTO;
import com.kbsw.message.entity.Message;
import com.kbsw.message.service.MessageService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {
	private final MessageService messageService;

	@PostMapping("/send")
	public ResponseEntity<Message> sendMessage(@RequestBody MessageRequestDTO requestDTO){
		return ResponseEntity.ok(messageService.sendMessage(requestDTO.getSenderId(), requestDTO.getReceiverId(), requestDTO.getContent()));
	}

	@GetMapping
	public ResponseEntity<List<Message>> getConversation(@RequestParam Long userId, @RequestParam Long otherUserId) {
		return ResponseEntity.ok(messageService.getConversation(userId, otherUserId));
	}

	@GetMapping("/chat-users")
	public ResponseEntity<List<MessageUserDTO>> getMessageUsers(@RequestParam Long userId){
		return ResponseEntity.ok(messageService.getMessageUsers(userId));
	}
}
