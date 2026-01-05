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

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ProxyController.class);

    private final WebClient webClient;
    private final int timeoutSeconds;

    private static final java.util.Set<String> FORBIDDEN_HEADERS = java.util.Set.of(
            HttpHeaders.HOST, HttpHeaders.AUTHORIZATION, "Cookie", HttpHeaders.CONTENT_LENGTH,
            "Transfer-Encoding", "Connection");

    public ProxyController(@Value("${proxy.timeout.seconds:10}") int timeoutSeconds) {
        this.timeoutSeconds = timeoutSeconds;

        HttpClient httpClient = HttpClient.create().responseTimeout(Duration.ofSeconds(timeoutSeconds));

        this.webClient = WebClient.builder().clientConnector(new ReactorClientHttpConnector(httpClient)).build();
    }

    @GetMapping(path = "/proxy", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> proxyGet(@RequestParam("url") String url,
            @RequestHeader MultiValueMap<String, String> headers) {
        if (url == null || url.isBlank()) {
            return ResponseEntity.badRequest().body("Invalid url");
        }

        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            return ResponseEntity.badRequest().body("Invalid url");
        }

        try {
            URI uri = URI.create(url);
            String host = uri.getHost();
            if (host == null) {
                return ResponseEntity.badRequest().body("Invalid url");
            }

            String lowerHost = host.toLowerCase();
            if (lowerHost.equals("localhost") || lowerHost.equals("127.0.0.1")
                    || lowerHost.startsWith("10.") || lowerHost.startsWith("192.168.")
                    || lowerHost.startsWith("172.")) {
                return ResponseEntity.badRequest().body("Forbidden host");
            }

            String body = webClient.method(HttpMethod.GET).uri(uri).headers(h -> {
                headers.forEach((k, v) -> {
                    if (!FORBIDDEN_HEADERS.contains(k)) {
                        h.put(k, v);
                    }
                });
            }).retrieve().bodyToMono(String.class).block(Duration.ofSeconds(timeoutSeconds));

            HttpHeaders respHeaders = new HttpHeaders();
            respHeaders.set(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
            return ResponseEntity.ok().headers(respHeaders).body(body == null ? "" : body);
        } catch (Exception ex) {
            log.error("Proxy error fetching url={}", url, ex);
            return ResponseEntity.status(502).body("Upstream request failed");
        }
    }
}
