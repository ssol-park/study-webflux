package com.webflux.study.exam03.async;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class AsyncApplication {

    @RestController
    public static class AsyncController {

        @GetMapping("/rest")
        public String rest() {
            return "rest";
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(AsyncApplication.class, args);
    }


}
