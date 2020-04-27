package com.fivestar.chatting.handler;

import java.util.Optional;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import com.fivestar.chatting.dto.ChatMessage;
import com.fivestar.chatting.pubsub.RedisPublisher;
import com.fivestar.chatting.repository.ChatRoomRepository;
import com.fivestar.chatting.service.ChatService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class StompHandler implements ChannelInterceptor {
	private final ChatRoomRepository chatRoomRepository;
	private final ChatService chatService;

	// websocket�� ���� ���� ��û�� ó�� �Ǳ��� ����ȴ�.
	@Override
	public Message<?> preSend(Message<?> message, MessageChannel channel) {
		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
		if (StompCommand.CONNECT == accessor.getCommand()) { // websocket �����û
		
		} else if (StompCommand.SUBSCRIBE == accessor.getCommand()) { // ä�÷� ������û
			// header�������� ���� destination������ ���, roomId�� �����Ѵ�.
			String roomId = chatService.getRoomId(
					Optional.ofNullable((String) message.getHeaders().get("simpDestination")).orElse("InvalidRoomId"));
			// ä�ù濡 ���� Ŭ���̾�Ʈ sessionId�� roomId�� ������ ���´�.(���߿� Ư�� ������ � ä�ù濡 �� �ִ��� �˱� ����)
			String sessionId = (String) message.getHeaders().get("simpSessionId");
			chatRoomRepository.setUserEnterInfo(sessionId, roomId);
			// ä�ù��� �ο����� +1�Ѵ�.
			chatRoomRepository.plusUserCount(roomId);
			// Ŭ���̾�Ʈ ���� �޽����� ä�ù濡 �߼��Ѵ�.(redis publish)
			String name ="wjddms";
			chatService.sendChatMessage(
					ChatMessage.builder().type(ChatMessage.MessageType.JOIN).roomId(roomId).sender(name).build());
			log.info("SUBSCRIBED {}, {}", name, roomId);
		} else if (StompCommand.DISCONNECT == accessor.getCommand()) { // Websocket ���� ����
			// ������ ����� Ŭ���̾�Ʈ sesssionId�� ä�ù� id�� ��´�.
			String sessionId = (String) message.getHeaders().get("simpSessionId");
			String roomId = chatRoomRepository.getUserEnterRoomId(sessionId);
			// ä�ù��� �ο����� -1�Ѵ�.
			chatRoomRepository.minusUserCount(roomId);
			// Ŭ���̾�Ʈ ���� �޽����� ä�ù濡 �߼��Ѵ�.(redis publish)
			String name ="wjddms";
			chatService.sendChatMessage(
					ChatMessage.builder().type(ChatMessage.MessageType.QUIT).roomId(roomId).sender(name).build());
			// ������ Ŭ���̾�Ʈ�� roomId ���� ������ �����Ѵ�.
			chatRoomRepository.removeUserEnterInfo(sessionId);
			log.info("DISCONNECTED {}, {}", sessionId, roomId);
		}
		return message;
	}
}
