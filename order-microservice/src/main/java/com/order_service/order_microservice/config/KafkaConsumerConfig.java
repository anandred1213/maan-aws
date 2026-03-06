package com.order_service.order_microservice.config;




import com.order_service.order_microservice.dto.UserEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConsumerConfig {

    public ConsumerFactory<String, UserEvent> consumerFactory(){


        Map<String,Object> config= new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,"localhost:9092");
        config.put(ConsumerConfig.GROUP_ID_CONFIG,"order-service-group");
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);

        // JsonDeserializer with target class
        JsonDeserializer<UserEvent> deserializer= new JsonDeserializer<>(UserEvent.class);
        deserializer.addTrustedPackages("*");
        return new DefaultKafkaConsumerFactory<>(config,
                new StringDeserializer(),deserializer);

    }

    public ConcurrentKafkaListenerContainerFactory<String, UserEvent> kafkaListenerContainerFactory(){
        ConcurrentKafkaListenerContainerFactory<String, UserEvent> factory=new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }
}
