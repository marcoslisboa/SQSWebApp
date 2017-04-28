package br.com.framework.sqs.config;

import br.com.framework.sqs.dto.PersonDTO;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.*;
import com.google.gson.Gson;

import javax.annotation.PostConstruct;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by Marcos Lisboa on 28/04/17.
 */
@Startup
@Singleton
public class SQSMessageReceive {

    private static final Logger log = Logger.getLogger(SQSMessageReceive.class.getName());

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

        ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(queueURL);
    }

    @Schedule(hour = "*", minute = "*", second = "10")
    public void receiveMessage() {
        ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(queueURL);
        List<Message> messages = amazonSQS.receiveMessage(receiveMessageRequest).getMessages();
        
        if (!messages.isEmpty()){
            for (Message message : messages) {
                log.info("  Message");
                log.info("    MessageId:     " + message.getMessageId());
                log.info("    ReceiptHandle: " + message.getReceiptHandle());
                log.info("    MD5OfBody:     " + message.getMD5OfBody());
                PersonDTO person = new Gson().fromJson(message.getBody(), PersonDTO.class);
                log.info("    Body:          " + person.toString());
                for (Map.Entry<String, String> entry : message.getAttributes().entrySet()) {
                    log.info("  Attribute");
                    log.info("    Name:  " + entry.getKey());
                    log.info("    Value: " + entry.getValue());
                }
                amazonSQS.deleteMessage(new DeleteMessageRequest(queueURL, message.getReceiptHandle()));
            }
        }
    }

}
