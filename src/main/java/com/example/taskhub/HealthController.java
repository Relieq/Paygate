package com.example.taskhub;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

@RestController
public class HealthController {
    record Info(String status, long timestamp) {}

    @GetMapping("/health")
    public Info healthCheck() {
        return new Info("ok", System.currentTimeMillis());
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
