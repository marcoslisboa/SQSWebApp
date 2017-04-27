package br.com.framework.sqs.config;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Stateless;
import javax.enterprise.context.Dependent;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

/**
 * Created by Marcos Lisboa on 27/04/17.
 */
@Stateless
public class SQSMessageSender {

    private MessageProducer producer = null;
    private Session senderSession = null;

    @PostConstruct
    public void createProducer() {
        try {
            senderSession = SQSMessageManager.connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            producer = senderSession.createProducer(SQSMessageManager.queue);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @PreDestroy
    public void destroy() {
        try {
            producer.close();
            senderSession.close();
        } catch (Exception e) {
            e.getStackTrace();
        }
    }

    public void sendMessage(String json) {
        try {
            TextMessage textMessage = senderSession.createTextMessage(json);
            producer.send(textMessage);
        } catch (Exception e) {
            e.getStackTrace();
        }
    }
}
