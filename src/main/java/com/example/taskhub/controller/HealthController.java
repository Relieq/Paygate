package com.example.taskhub.controller;

import com.example.taskhub.service.TimeService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

@RestController
public class HealthController {
    private final TimeService time;

    public HealthController(TimeService time) {
        this.time = time;
    }

    record Health(String status, long timeMs) {}

    @GetMapping("/health")
    public Health healthCheck() {
        return new Health("ok", time.now());
    }

    // Example: http://localhost:8080/echo?msg=hello
    @GetMapping("/echo")
    public String echo(@RequestParam String msg) {
        return msg;
    }

    @GetMapping("/square/{n}")
    public Map<String, Object> square(@PathVariable int n) {
        return Map.of("n", n, "square", n * n);
    }
}
