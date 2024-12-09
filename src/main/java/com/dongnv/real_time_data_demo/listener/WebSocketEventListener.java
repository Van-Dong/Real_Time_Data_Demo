package com.dongnv.real_time_data_demo.listener;

import com.dongnv.real_time_data_demo.constant.MessageType;
import com.dongnv.real_time_data_demo.model.ChatMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketEventListener {  // For WebSocket
    private final SimpMessageSendingOperations messagingTemplate; // Để gửi message khi có người leave

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) { // chắc dựa vào tham số của hàm để xác định sự kiện
        log.info("Received a new web socket connection");
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String username = (String) headerAccessor.getSessionAttributes().get("username");
        // do khi một client kết nối đến thì ta đã thêm thuộc tính username vào Session WebSocket rồi
        // => có thể lấy được nó khi client ngắt kết nối

        if (username != null) {
            log.info("User disconnected: {}", username);

            var chatMessage = ChatMessage.builder()
                    .type(MessageType.LEAVE)
                    .sender(username)
                    .build();

            messagingTemplate.convertAndSend("/topic/public", chatMessage);
        }
    }
}
