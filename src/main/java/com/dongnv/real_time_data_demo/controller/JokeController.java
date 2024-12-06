package com.dongnv.real_time_data_demo.controller;

import com.dongnv.real_time_data_demo.model.RandomDataFromInternet;
import com.dongnv.real_time_data_demo.service.FetchDataService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@RestController
@RequestMapping("/sse-server")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JokeController {
    FetchDataService fetchDataService;

    // CopyOnWriteArrayList là một triển khai của List, lớp này thiết kế đặc biệt để sử dụng trong
    // môi trường đa luồng mà không cần đồng bộ hóa thủ công
    // Danh sách lưu trữ các kết nối client
    CopyOnWriteArrayList<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    @CrossOrigin(origins = "*")
    @GetMapping(value = "/sse-with-my-data", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> streamJokes() { // Chỉ dùng cho các giá trị Block
        return Flux.interval(Duration.ofSeconds(5))
                .map(sequence -> ServerSentEvent.<String>builder()
                        .data(LocalDateTime.now().toString())
                        .build());
    }


    // Cái này t chưa biết cách cấu hình nó là ServerSentEvent (nếu là clas này thì ta có thể cấu hình 1 số thông số cho sự kiện)
    @CrossOrigin(origins = "*")
    @GetMapping(value = "/sse-with-fetch-data", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<RandomDataFromInternet> fetchJokes() {  // Dành cho giá trị Async (như Mono, Flux)
        return Flux.interval(Duration.ofSeconds(10))
                .flatMap(tick -> fetchDataService.getRandomChuckNorrisJoke()
                        .map(data -> data));
    }

    @CrossOrigin(origins = "*")
    @GetMapping(value = "/group-sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter registerSSEGroup() {  // Các Client nhận dữ liệu giống nhau
        SseEmitter emitter = new SseEmitter();
        emitters.add(emitter);

        // Khi client ngắt kết nối, loại bỏ emitter khỏi danh sách
        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));

        return emitter;
    }


    // Phương thức gửi tin nhắn cho tất cả các client
    public void sendToAllClients(String message) {
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(message);  // Gửi tin nhắn cho mỗi client
            } catch (IOException e) { // vẫn thể có lỗi khi đang truyền dữ liệu nhưng tab client bị đóng
                emitters.remove(emitter); // Nếu gửi lỗi, loại bỏ emitter
            }
        }
    }

    // Phương thức này được gọi mỗi 10 giây để gửi thông báo đến tất cả client
    @Scheduled(fixedRate = 10000) // Gửi thông báo mỗi 10 giây (10000 milliseconds)
    public void sendPeriodicNotification() {
        log.info("SIZE CLIENT: " + emitters.size());
        RandomDataFromInternet value = fetchDataService.getRandomChuckNorrisJoke().block();
        sendToAllClients(value.getValue());
    }
}
