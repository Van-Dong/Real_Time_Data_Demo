package com.dongnv.real_time_data_demo.controller;

import com.dongnv.real_time_data_demo.model.ChatMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {

    @MessageMapping("/sendMessage")  // Các tin nhắn sẽ gửi đến /app/sendMessage (prefix /app trước đã đặt) => @MessageMapping là giúp client (publisher) gửi message đến server (
    @SendTo("/topic/public")  // Gửi trả kết quả đến tất cả subcribers của /topic/public  => @SendTo là từ Server đến Client (Server gửi đến các Subscriber đã đăng ký)
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
        return chatMessage;  // nhận tin nhắn từ client (publisher) sau đó đẩy cho các subscribers luôn
    }

    @MessageMapping("/addUser")
    @SendTo("/topic/public")
    public ChatMessage addUser(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {

        // Session Socket được tạo và tồn tại trong suốt vòng đời của kết nối WebSocket của client
        // => thêm thuộc tính vào Session Socket thì thuộc tính này sẽ tồn tại trong suốt vòng đời kết nối WebSocket của client
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
        return chatMessage;
    }
}
