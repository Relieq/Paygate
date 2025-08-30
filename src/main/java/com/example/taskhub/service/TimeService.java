package com.example.taskhub.service;

import org.springframework.stereotype.Service;

@Service
public class TimeService {
    public long now() {
        return System.currentTimeMillis();
    }
}
