package com.techmate.techmate.infrastructure.web;

import java.net.URI;
import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.netty.http.client.HttpClient;

@RestController
public class ProxyController {

    private final WebClient webClient;

    @Value("${proxy.timeout.seconds:10}")
    private int timeoutSeconds;

    public ProxyController() {
        HttpClient httpClient = HttpClient.create()
                .responseTimeout(Duration.ofSeconds(10));

        this.webClient = WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }

    @GetMapping(path = "/proxy", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> proxyGet(@RequestParam("url") String url,
                                           @RequestHeader MultiValueMap<String, String> headers) {
        // Basic safety: allow only http/https and avoid local addresses
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            return ResponseEntity.badRequest().body("Invalid url");
        }

        try {
            String body = webClient.method(HttpMethod.GET)
                    .uri(URI.create(url))
                    .headers(h -> h.addAll(headers.toSingleValueMap()))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block(Duration.ofSeconds(timeoutSeconds));

            HttpHeaders respHeaders = new HttpHeaders();
            respHeaders.set(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
            return ResponseEntity.ok().headers(respHeaders).body(body);
        } catch (Exception ex) {
            return ResponseEntity.status(502).body("Upstream request failed: " + ex.getMessage());
        }
    }
}
