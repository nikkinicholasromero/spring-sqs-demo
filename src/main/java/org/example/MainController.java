package org.example;

import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.model.AmazonSQSException;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


@RestController
public class MainController {
    @Autowired
    private AmazonSQSAsync amazonSQSAsync;

    @Autowired
    private QueueMessagingTemplate queueMessagingTemplate;

    @PostMapping("/{message}")
    public void addQueue(@PathVariable String message) {
        String queueName = "MyQueue.fifo";
        initializeQueue(queueName);

        Map<String, Object> headers = new HashMap<>();
        headers.put("message-group-id", "QUEUE");
        headers.put("message-deduplication-id", UUID.randomUUID().toString());

        queueMessagingTemplate.convertAndSend(queueName, message, headers);
    }

    public void initializeQueue(String name) {
        CreateQueueRequest createRequest = new CreateQueueRequest(name)
                .addAttributesEntry("FifoQueue", "true")
                .addAttributesEntry("ContentBasedDeduplication", "true");

        try {
            amazonSQSAsync.createQueue(createRequest);
        } catch (AmazonSQSException e) {
            if (!e.getErrorCode().equals("QueueAlreadyExists")) {
                throw e;
            }
        }
    }
}
