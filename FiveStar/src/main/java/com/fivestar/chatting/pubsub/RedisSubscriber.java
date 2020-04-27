package com.fivestar.chatting.pubsub;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fivestar.chatting.dto.ChatMessage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class RedisSubscriber implements MessageListener {

	private final ObjectMapper objectMapper;
	private final RedisTemplate<?, ?> redisTemplate;
	private final SimpMessageSendingOperations messagingTemplate;

	@Override
	public void onMessage(Message message, byte[] pattern) {
		try {
			String publishMessage = (String) redisTemplate.getStringSerializer().deserialize(message.getBody());
			ChatMessage roomMessage = objectMapper.readValue(publishMessage, ChatMessage.class);
			messagingTemplate.convertAndSend("/sub/chat/room/" + roomMessage.getRoomId(), roomMessage);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}

	public void sendMessage(String publishMessage) {
		try {
			// ChatMessage 객채로 맵핑
			ChatMessage chatMessage = objectMapper.readValue(publishMessage, ChatMessage.class);
			// 채팅방을 구독한 클라이언트에게 메시지 발송
			messagingTemplate.convertAndSend("/sub/chat/room/" + chatMessage.getRoomId(), chatMessage);
		} catch (Exception e) {
			log.error("Exception {}", e);
		}
	}
}