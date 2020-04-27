package com.fivestar.chatting.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

import com.fivestar.chatting.dto.ChatMessage;
import com.fivestar.chatting.repository.ChatRoomRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ChatService {
    private final ChannelTopic channelTopic;
    private final RedisTemplate redisTemplate;
    private final ChatRoomRepository chatRoomRepository;
    /**
     * destination�������� roomId ����
     */
    public String getRoomId(String destination) {
        int lastIndex = destination.lastIndexOf('/');
        if (lastIndex != -1)
            return destination.substring(lastIndex + 1);
        else
            return "";
    }
    /**
     * ä�ù濡 �޽��� �߼�
     */
    public void sendChatMessage(ChatMessage chatMessage) {
        chatMessage.setUserCount(chatRoomRepository.getUserCount(chatMessage.getRoomId()));
        if (ChatMessage.MessageType.JOIN.equals(chatMessage.getType())) {
            chatMessage.setMessage(chatMessage.getSender() + "���� �濡 �����߽��ϴ�.");
            chatMessage.setSender("[�˸�]");
        } else if (ChatMessage.MessageType.QUIT.equals(chatMessage.getType())) {
            chatMessage.setMessage(chatMessage.getSender() + "���� �濡�� �������ϴ�.");
            chatMessage.setSender("[�˸�]");
        }
        redisTemplate.convertAndSend(channelTopic.getTopic(), chatMessage);
    }
}