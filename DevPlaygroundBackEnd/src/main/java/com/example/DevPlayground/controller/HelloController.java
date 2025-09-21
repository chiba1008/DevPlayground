package com.example.DevPlayground.controller;

import com.example.DevPlayground.model.HelloResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/api/hello-world")
    public HelloResponse helloWorld() {
        return new HelloResponse("Hello, World!", "success");
    }
}
