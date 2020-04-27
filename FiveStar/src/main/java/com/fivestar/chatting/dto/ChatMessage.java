package com.fivestar.chatting.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ChatMessage {
    public enum MessageType {
        JOIN, TALK, QUIT
    }
    private MessageType type;
    private String roomId;
    private String sender;
    private String message;
    private long userCount;
    private String profileurl;
  
    @Builder
    public ChatMessage(MessageType type, String roomId, String sender, String message, String profileurl, long userCount) {
        this.type = type;
        this.roomId = roomId;
        this.sender = sender;
        this.message = message;
        this.profileurl = profileurl;
        this.userCount = userCount;
    }

	@Override
	public String toString() {
		return "ChatMessage [type=" + type + ", roomId=" + roomId + ", sender=" + sender + ", message=" + message
				+ ", userCount=" + userCount + ", profileurl=" + profileurl + "]";
	}
}
