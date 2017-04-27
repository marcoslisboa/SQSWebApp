package br.com.framework.sqs.config;

import com.amazon.sqs.javamessaging.SQSConnection;
import com.amazon.sqs.javamessaging.SQSConnectionFactory;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.jms.Queue;
import javax.jms.Session;

/**
 * Created by Marcos Lisboa on 27/04/17.
 */
@Singleton
public class SQSMessageManager {

    public static final String ACCESS_KEY = "AKIAI3HENRF5UGS6FUOQ";
    public static final String SECRET_KEY = "mlGRKvySEVM5MD5YuaKRmhDrFm/4pyK0Ua7mruv5";
    public static final String QUEUE_NAME = "ExampleQueueDeveloper";

    public static SQSConnection connection = null;
    public static Queue queue = null;

    @Inject
    SQSMessageReceiver sqsMessageReceiver;

    public void init() {
        try {
            BasicAWSCredentials credentials = new BasicAWSCredentials(ACCESS_KEY, SECRET_KEY);
            SQSConnectionFactory connectionFactory =
                    SQSConnectionFactory.builder()
                            .withRegion(Region.getRegion(Regions.US_WEST_2))
                            .withAWSCredentialsProvider(new AWSStaticCredentialsProvider(credentials))
                            .build();

            connection = connectionFactory.createConnection();
            connection
                    .createSession(false, Session.AUTO_ACKNOWLEDGE)
                    .createConsumer(queue)
                    .setMessageListener(sqsMessageReceiver);

            connection.start();
        } catch (Exception e) {
            e.getStackTrace();
        }
    }
}