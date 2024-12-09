package com.dongnv.real_time_data_demo.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

// @EnableWebSocketMessageBroker kích hoạt cấu hình message broker (Simple Broker)
// và giúp tích hợp WebSocket với Spring Message với @MessageMapping và @SendTo trong controller
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Đăng ký STOMP endpoint để client có thể kết nối tới server thông qua WebSocket
        registry.addEndpoint("/ws")
                .setAllowedOrigins("http://127.0.0.1:5500")  // websocket không dùng @CrossOrigin được
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Định nghĩa prefix cho các message từ client gửi đến server (Publisher gửi lên server ý)
        registry.setApplicationDestinationPrefixes("/app");

        // Kích hoạt Message Broker nội bộ
        // /topic và /queue là destination prefix được Simple Broker sử dụng để định tuyến tin nhắn từ server đến các Subscriber
        //   => client subcribe topic thông qua các địa chỉ này
        registry.enableSimpleBroker("/topic", "queue");
    }
}
