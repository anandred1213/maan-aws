package com.order_service.order_microservice.config;


/*
* this class is set up for asynchronous communication via @Async annotation
*
* */

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class AsyncService {

//    @Async
//    public  void processOrder(long orderId ){
//        System.out.println("started async "+orderId);
//    }

    @Async
    public void processOrder(long orderId) {
        System.out.println("started async: " + orderId);
        try {
            Thread.sleep(3000);
        } catch (Exception e) {}

        System.out.println("completed async: " + orderId);
    }

}
