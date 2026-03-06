package com.order_service.order_microservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableDiscoveryClient
//@EnableFeignClients
@EnableAsync  //1. for asynchronous via @Async
public class OrderMicroserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrderMicroserviceApplication.class, args);
	}

}
