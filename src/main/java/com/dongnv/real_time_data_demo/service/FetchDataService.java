package com.dongnv.real_time_data_demo.service;

import com.dongnv.real_time_data_demo.model.RandomDataFromInternet;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FetchDataService {
    WebClient webClient;
    public FetchDataService() {
        this.webClient = WebClient.builder()
                .baseUrl("https://api.chucknorris.io")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public Mono<RandomDataFromInternet> getRandomChuckNorrisJoke() {
        RandomDataFromInternet joke = new RandomDataFromInternet();
        return webClient.get()
                .uri("/jokes/random")
                .retrieve()
                .bodyToMono(RandomDataFromInternet.class);
    }
}
