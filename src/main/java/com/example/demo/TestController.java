package com.example.demo;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

@RestController
@RequiredArgsConstructor
public class TestController {

    private final WebClient webClient;

    @GetMapping("/delay1")
    public String test() throws InterruptedException {
        Thread.sleep(1000L);
        return "Hello World! ";
    }

    @GetMapping("/delay5")
    public String test5() throws InterruptedException {
        Thread.sleep(5000L);
        return "Hello World! ";
    }

    @GetMapping("/delay10")
    public String test10() throws InterruptedException {
        Thread.sleep(10000L);
        return "Hello World! ";
    }

    //  34.47.119.193:8080/test GET 요청을 보낸다
    @GetMapping("/call")
    public String call() {
        System.out.println("==================================================================");
        return webClient.get()
                .uri("http://34.47.119.193:8080/test")
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}
