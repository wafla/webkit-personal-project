package com.kbsw.message.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.kbsw.message.entity.Message;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
	@Query("SELECT m FROM Message m WHERE (m.sender.id = :userId AND m.receiver.id = :otherUserId) OR (m.sender.id = :otherUserId AND m.receiver.id = :userId) ORDER BY m.wdate ASC")
	List<Message> findConversation(@Param("userId") Long userId, @Param("otherUserId") Long otherUserId);

	@Query("SELECT DISTINCT CASE WHEN m.sender.id = :userId THEN m.receiver.id ELSE m.sender.id END FROM Message m " +
		"WHERE m.sender.id = :userId OR m.receiver.id = :userId")
	List<Long> findMessageUserIds(Long userId);

	@Query("SELECT m FROM Message m WHERE (m.sender.id = :userId AND m.receiver.id = :otherUserId) " +
		"OR (m.sender.id = :otherUserId AND m.receiver.id = :userId) ORDER BY m.wdate DESC LIMIT 1")
	Message findLastMessage(Long userId, Long otherUserId);
}
