package com.fivestar.chatting.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import com.fivestar.chatting.dto.ChatMessage;
import com.fivestar.chatting.pubsub.RedisPublisher;
import com.fivestar.chatting.repository.ChatRoomRepository;
import com.fivestar.chatting.service.ChatService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class ChatController {
	private final RedisPublisher redisPublisher;
	private final ChatRoomRepository chatRoomRepository;
	private final ChatService chatService;

	@MessageMapping("/chat/message")
	public void message(ChatMessage message) {
		System.out.println(message.toString());

		// 로그인 회원 정보로 대화명 설정
		message.setSender("wjddms");
		// 채팅방 인원수 세팅
		message.setUserCount(chatRoomRepository.getUserCount(message.getRoomId()));
		// Websocket에 발행된 메시지를 redis로 발행(publish)
		chatService.sendChatMessage(message);
	}
}
