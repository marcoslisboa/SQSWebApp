package br.com.framework.sqs.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import java.util.HashMap;
import java.util.logging.Logger;

/**
 * Created by Marcos Lisboa on 28/04/17.
 */
@Startup
@Singleton
public class SQSMessageSender {

    private static final Logger log = Logger.getLogger(SQSMessageSender.class.getName());

    private static final String ACCESS_KEY = "AKIAI3HENRF5UGS6FUOQ";
    private static final String SECRET_KEY = "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";
    private static final String SERVICE_ENDPOINT = "https://sqs.us-west-2.amazonaws.com";
    private static final String PERSON_GROUP = "PersonGroup";
    private static final String QUEUE_NAME = "PersonFifoQueue.fifo";

    private String queueURL;
    private AmazonSQS amazonSQS;

    @PostConstruct
    public void init() {
        amazonSQS = AmazonSQSClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(ACCESS_KEY, SECRET_KEY)))
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(SERVICE_ENDPOINT, Regions.US_WEST_2.getName()))
                .build();

        CreateQueueRequest queueRequest = new CreateQueueRequest(QUEUE_NAME).withAttributes(new HashMap<String, String>() {{
            put("FifoQueue", "true");
            put("ContentBasedDeduplication", "true");
        }});

        queueURL = amazonSQS.createQueue(queueRequest).getQueueUrl();
    }

    public void sendMessage(String message) {
        SendMessageRequest sendMessageRequest = new SendMessageRequest(queueURL, message);
        sendMessageRequest.setMessageGroupId(PERSON_GROUP);
        SendMessageResult sendMessageResult = amazonSQS.sendMessage(sendMessageRequest);

        log.info("SendMessage succeed!!!");
        log.info("Sequence Number: " + sendMessageResult.getSequenceNumber());
        log.info("Message Id: " + sendMessageResult.getMessageId());
    }

}
