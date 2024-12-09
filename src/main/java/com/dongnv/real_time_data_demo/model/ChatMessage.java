package com.dongnv.real_time_data_demo.model;

import com.dongnv.real_time_data_demo.constant.MessageType;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChatMessage { // for WebSocket
    MessageType type;
    String content;
    String sender;
}
