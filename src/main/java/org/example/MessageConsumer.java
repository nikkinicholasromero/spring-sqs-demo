package org.example;

import org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.stereotype.Component;

@Component
public class MessageConsumer {
    @SqsListener(value = "MyQueue.fifo", deletionPolicy = SqsMessageDeletionPolicy.ON_SUCCESS)
    public void consume(String message) {
        System.out.println(message);
    }
}
