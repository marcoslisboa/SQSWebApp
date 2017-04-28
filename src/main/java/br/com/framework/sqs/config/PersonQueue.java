package br.com.framework.sqs.config;

import br.com.framework.sqs.config.base.BasicSQSQueue;
import br.com.framework.sqs.dto.PersonDTO;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.google.gson.Gson;

import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

/**
 * Created by Marcos Lisboa on 28/04/17.
 */
@Startup
@Singleton
public class PersonQueue extends BasicSQSQueue {

    private static final Logger log = Logger.getLogger(PersonQueue.class.getName());

    public void sendMessage(PersonDTO person) {
        send(new SendMessageRequest(queueURL, gson.toJson(person)));
    }

    @Schedule(hour = "*", minute = "*", second = "10")
    public void receiveMessage() {
        log.info("  Read SQS Queue - " + getQueueName());

        ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(queueURL);
        List<Message> messages = sqs.receiveMessage(receiveMessageRequest).getMessages();

        if (isNotEmpty(messages)) {
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
                sqs.deleteMessage(new DeleteMessageRequest(queueURL, message.getReceiptHandle()));
            }
        }
    }

    @Override
    public String getQueueName() {
        return "PersonFifoQueue.fifo";
    }

    @Override
    public String getMessageGroupId() {
        return "PersonGroup";
    }
}
