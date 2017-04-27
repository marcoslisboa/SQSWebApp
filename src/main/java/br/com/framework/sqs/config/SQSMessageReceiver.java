package br.com.framework.sqs.config;

import javax.enterprise.context.Dependent;
import javax.jms.Message;
import javax.jms.MessageListener;

/**
 * Created by Marcos Lisboa on 27/04/17.
 */
public class SQSMessageReceiver implements MessageListener {
    public void onMessage(Message message) {
        System.out.println(message);
    }
}
