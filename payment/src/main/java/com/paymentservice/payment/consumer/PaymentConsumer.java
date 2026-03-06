package com.paymentservice.payment.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class PaymentConsumer {

    private static final Logger log = LoggerFactory.getLogger(PaymentConsumer.class);

    @KafkaListener(topics = "order-topic", groupId = "payment-group")
    public void consumeOrder(String message) {
        try {
            log.info("Payment service received order event: {}", message);
            
            if (message == null || message.trim().isEmpty()) {
                log.warn("Received empty or null message");
                return;
            }
            
            log.info("💳 Processing payment for order...");
            // Payment processing logic here
            
            log.info("Payment processed successfully for order: {}", message);
        } catch (IllegalArgumentException e) {
            log.error("Invalid order data: {}", message, e);
            throw e;
        } catch (org.springframework.kafka.support.serializer.DeserializationException e) {
            log.error("Failed to deserialize message: {}", message, e);
            throw e;
        } catch (RuntimeException e) {
            log.error("Error processing payment for order: {}", message, e);
            throw e;
        }
    }
}
