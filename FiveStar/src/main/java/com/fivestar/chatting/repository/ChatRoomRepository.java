package com.fivestar.chatting.repository;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Repository;

import com.fivestar.chatting.dto.ChatRoom;
import com.fivestar.chatting.pubsub.RedisSubscriber;

import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@Repository
public class ChatRoomRepository {

	 // Redis CacheKeys
    private static final String CHAT_ROOMS = "CHAT_ROOM"; // ä�÷� ����
    public static final String USER_COUNT = "USER_COUNT"; // ä�÷뿡 ������ Ŭ���̾�Ʈ�� ����
    public static final String ENTER_INFO = "ENTER_INFO"; // ä�÷뿡 ������ Ŭ���̾�Ʈ�� sessionId�� ä�÷� id�� ������ ���� ����
    @Resource(name = "redisTemplate")
    private HashOperations<String, String, ChatRoom> hashOpsChatRoom;
    @Resource(name = "redisTemplate")
    private HashOperations<String, String, String> hashOpsEnterInfo;
    @Resource(name = "redisTemplate")
    private ValueOperations<String, String> valueOps;
    // ��� ä�ù� ��ȸ
    public List<ChatRoom> findAllRoom() {
        return hashOpsChatRoom.values(CHAT_ROOMS);
    }
    // Ư�� ä�ù� ��ȸ
    public ChatRoom findRoomById(String id) {
        return hashOpsChatRoom.get(CHAT_ROOMS, id);
    }
    // ä�ù� ���� : ������ ä�ù� ������ ���� redis hash�� �����Ѵ�.
    public ChatRoom createChatRoom(String name) {
        ChatRoom chatRoom = ChatRoom.create(name);
        hashOpsChatRoom.put(CHAT_ROOMS, chatRoom.getRoomId(), chatRoom);
        return chatRoom;
    }
    // ������ ������ ä�ù�ID�� ���� ����ID ���� ���� ����
    public void setUserEnterInfo(String sessionId, String roomId) {
        hashOpsEnterInfo.put(ENTER_INFO, sessionId, roomId);
    }
    // ���� �������� ������ �ִ� ä�ù� ID ��ȸ
    public String getUserEnterRoomId(String sessionId) {
        return hashOpsEnterInfo.get(ENTER_INFO, sessionId);
    }
    // ���� ���������� ���ε� ä�ù�ID ����
    public void removeUserEnterInfo(String sessionId) {
        hashOpsEnterInfo.delete(ENTER_INFO, sessionId);
    }
    // ä�ù� ������ ��ȸ
    public long getUserCount(String roomId) {
        return Long.valueOf(Optional.ofNullable(valueOps.get(USER_COUNT + "_" + roomId)).orElse("0"));
    }
    // ä�ù濡 ������ ������ +1
    public long plusUserCount(String roomId) {
        return Optional.ofNullable(valueOps.increment(USER_COUNT + "_" + roomId)).orElse(0L);
    }
    // ä�ù濡 ������ ������ -1
    public long minusUserCount(String roomId) {
        return Optional.ofNullable(valueOps.decrement(USER_COUNT + "_" + roomId)).filter(count -> count > 0).orElse(0L);
    }
}
