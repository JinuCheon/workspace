package com.example.demo;


import io.netty.channel.ChannelOption;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

import java.time.Duration;

@Configuration
class WebclientConfig {
    // 10초 이상 idle 상태인 connection 은 close
    private static final int MAX_IDLE_CONNECTION_TIME = 10000;
    // 30초 이상 커넥션 시도 시도
    private static final int TRY_CONNECTION_TIMEOUT_LIMIT = 30000;
    // 30초 이상 응답이 없으면 커넥션 강제 종료
    private static final int RESPONSE_TIMEOUT_LIMIT = 30000;

    @Bean
    public WebClient buildWebClient() {
        final ConnectionProvider provider = ConnectionProvider.builder("webClientConnectionProvider")
                .maxIdleTime(Duration.ofMillis(MAX_IDLE_CONNECTION_TIME))
                .build();

        final HttpClient httpClient = HttpClient.create(provider)
                .keepAlive(false)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, TRY_CONNECTION_TIMEOUT_LIMIT)
                .responseTimeout(Duration.ofMillis(RESPONSE_TIMEOUT_LIMIT));

        final ClientHttpConnector connector = new ReactorClientHttpConnector(httpClient);

        return WebClient
                .builder()
                .clientConnector(connector)
                .filter(logRequest())
                .filter(logResponse())
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(1024 * 1024 * 3))
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    private ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            System.out.println("=== Request Headers ===");
            System.out.println("Request Method: " + clientRequest.method());
            System.out.println("Request URL: " + clientRequest.url());
            clientRequest.headers().forEach((name, values) -> {
                values.forEach(value -> {
                    System.out.println(name + ": " + value);
                });
            });
            return Mono.just(clientRequest);
        });
    }

    private ExchangeFilterFunction logResponse() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            System.out.println("=== Response Headers ===");
            System.out.println("Status Code: " + clientResponse.statusCode());
            clientResponse.headers().asHttpHeaders().forEach((name, values) -> {
                values.forEach(value -> {
                    System.out.println(name + ": " + value);
                });
            });
            return Mono.just(clientResponse);
        });
    }
}
