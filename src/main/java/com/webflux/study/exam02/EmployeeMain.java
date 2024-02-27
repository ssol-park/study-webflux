package com.webflux.study.exam02;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoReactiveAutoConfiguration;

@SpringBootApplication(exclude = MongoReactiveAutoConfiguration.class)
public class EmployeeMain {
    public static void main(String[] args) {
        SpringApplication.run(EmployeeMain.class);

        EmployeeWebClient webClient = new EmployeeWebClient();
        webClient.consume();
    }
}
