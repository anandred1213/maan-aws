package com.order_service.order_microservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    public NewTopic orderTopic(){
        return TopicBuilder.name("order-topic")
                .partitions(1)
                .replicas(1)
                .build();
    }

}
