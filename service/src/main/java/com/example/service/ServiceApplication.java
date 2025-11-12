package com.example.service;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@SpringBootApplication
public class ServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServiceApplication.class, args);
	}

    @Bean
    MethodToolCallbackProvider methodToolCallbackProvider (DogAdoptionScheduler scheduler){
        return MethodToolCallbackProvider
                .builder()
                .toolObjects(scheduler)
                .build() ;
    }
}

@Component
class DogAdoptionScheduler {

    @Tool(description = "schedule an appointment to pick up a dog from a Pooch Palace location")
    String schedule(@ToolParam(description = "the id of the dog") int dogId, @ToolParam(description = "the name of the dog") String dogName) {
        var i = Instant.now().plus(3, ChronoUnit.DAYS).toString();
        IO.println("scheduling " + dogName + "/" + dogId + " for " + i);
        return i;
    }

}

