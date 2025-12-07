package com.example.tam;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableRabbit
@ComponentScan(basePackages = "com.example.tam")
public class TamApplication {

	public static void main(String[] args) {
		SpringApplication.run(TamApplication.class, args);
	}

}
