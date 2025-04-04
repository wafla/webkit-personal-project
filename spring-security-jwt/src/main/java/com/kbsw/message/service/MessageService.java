package com.kbsw.message.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.kbsw.message.dto.MessageDTO;
import com.kbsw.message.dto.MessageUserDTO;
import com.kbsw.message.entity.Message;
import com.kbsw.message.repository.MessageRepository;
import com.kbsw.user.entity.MemberEntity;
import com.kbsw.user.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageService {
	private final MessageRepository messageRepository;
	private final MemberRepository memberRepository;

	public Message sendMessage(Long senderId, Long receiverId, String content) {
		MemberEntity sender = memberRepository.findById(senderId)
			.orElseThrow(() -> new RuntimeException("발신자를 찾을 수 없음"));
		MemberEntity receiver = memberRepository.findById(receiverId)
			.orElseThrow(()->new RuntimeException("수신자를 찾을 수 없음"));

		log.info("messageService");

		Message message = Message.builder()
			.sender(sender)
			.receiver(receiver)
			.content(content)
			.build();
		return messageRepository.save(message);
	}

	public List<Message> getConversation(Long userId, Long otherUserId) {
		return messageRepository.findConversation(userId, otherUserId);
	}

	public List<MessageUserDTO> getMessageUsers(Long userId) {
		List<Long> userIds = messageRepository.findMessageUserIds(userId);
		return memberRepository.findAllById(userIds)
			.stream()
			.map(user -> {
				Message lastMessage = messageRepository.findLastMessage(userId, user.getId());
				return new MessageUserDTO(
					user.getId(),
					user.getName(),
					user.getFileName(),
					lastMessage != null ? lastMessage.getContent() : "메시지가 없습니다.",
					lastMessage != null ? lastMessage.getWdate().toString() : ""
				);
			})
			.toList();
	}
}
