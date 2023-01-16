package com.study.newtobby;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
//@EnableJpaAuditing
public class NewTobbyApplication {

    public static void main(String[] args) {
        SpringApplication.run(NewTobbyApplication.class, args);
    }

}
