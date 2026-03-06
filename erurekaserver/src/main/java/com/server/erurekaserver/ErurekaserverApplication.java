package com.server.erurekaserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class ErurekaserverApplication {

	public static void main(String[] args) {
		SpringApplication.run(ErurekaserverApplication.class, args);
	}

}
