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

		// �α��� ȸ�� ������ ��ȭ�� ����
		message.setSender("wjddms");
		// ä�ù� �ο��� ����
		message.setUserCount(chatRoomRepository.getUserCount(message.getRoomId()));
		// Websocket�� ����� �޽����� redis�� ����(publish)
		chatService.sendChatMessage(message);
	}
}
