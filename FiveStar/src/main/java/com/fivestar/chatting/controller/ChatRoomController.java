package com.fivestar.chatting.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fivestar.chatting.dto.ChatRoom;
import com.fivestar.chatting.repository.ChatRoomRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
@CrossOrigin(origins ="*")
@RequestMapping("/chat")
public class ChatRoomController {
    private final ChatRoomRepository chatRoomRepository;
    // ä�� ����Ʈ ȭ��
    @GetMapping("/room")
    public String rooms(Model model) {
        return "/chat/room";
    }
    // ��� ä�ù� ��� ��ȯ
    @GetMapping("/rooms")
    @ResponseBody
    public List<ChatRoom> room() {
    	 List<ChatRoom> chatRooms = chatRoomRepository.findAllRoom();
         chatRooms.stream().forEach(room -> room.setUserCount(chatRoomRepository.getUserCount(room.getRoomId())));
         return chatRooms;
    }
    // ä�ù� ����
    @PostMapping("/room")
    @ResponseBody
    public ChatRoom createRoom(@RequestParam String name) {
    
        return chatRoomRepository.createChatRoom(name);
    }
    // ä�ù� ���� ȭ��
    @GetMapping("/room/enter/{roomId}")
    public String roomDetail(Model model, @PathVariable String roomId) {
        model.addAttribute("roomId", roomId);
        return "/chat/roomdetail";
    }
    // Ư�� ä�ù� ��ȸ
    @GetMapping("/room/{roomId}")
    @ResponseBody
    public ChatRoom roomInfo(@PathVariable String roomId) {
        return chatRoomRepository.findRoomById(roomId);
    }
}
