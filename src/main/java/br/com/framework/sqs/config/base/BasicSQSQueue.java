package br.com.framework.sqs.config.base;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;
import java.util.logging.Logger;

/**
 * Created by Marcos Lisboa on 28/04/17.
 */
public abstract class BasicSQSQueue {

    private static final Logger log = Logger.getLogger(BasicSQSQueue.class.getName());

    private static final String ACCESS_KEY = "";
    private static final String SECRET_KEY = "";
    private static final String SERVICE_ENDPOINT = "https://sqs.us-west-2.amazonaws.com";

    protected Gson gson = new GsonBuilder().create();
    protected String queueURL;
    protected AmazonSQS sqs;

    public BasicSQSQueue() {
        sqs = getSQSClient();
        queueURL = createQueueURL();
    }

    public abstract String getQueueName();

    public abstract String getMessageGroupId();

    private String createQueueURL() {
        CreateQueueRequest queueRequest = new CreateQueueRequest(getQueueName())
                .withAttributes(new HashMap<String, String>() {{
                    put("FifoQueue", "true");
                    put("ContentBasedDeduplication", "true");
                }});
        return sqs.createQueue(queueRequest).getQueueUrl();
    }

    private AmazonSQS getSQSClient() {
        return AmazonSQSClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(ACCESS_KEY, SECRET_KEY)))
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(SERVICE_ENDPOINT, Regions.US_WEST_2.getName()))
                .build();
    }

    protected void send(SendMessageRequest request) {
        request.setMessageGroupId(getMessageGroupId());
        SendMessageResult result = sqs.sendMessage(request);

        log.info("SendMessage succeed!!!");
        log.info("Sequence Number: " + result.getSequenceNumber());
        log.info("Message Id: " + result.getMessageId());
    }
}
