package br.com.framework.sqs.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.enterprise.context.Dependent;
import javax.jms.Message;
import javax.jms.MessageListener;
import java.util.logging.Logger;

/**
 * Created by Marcos Lisboa on 27/04/17.
 */
@Dependent
public class SQSMessageReceiver implements MessageListener {

    private static final Logger log = Logger.getLogger(SQSMessageReceiver.class.getName());
    private static final Gson gson = new GsonBuilder().create();

    public void onMessage(Message message) {
        log.info(message.toString());
        log.info(gson.toJson(message));
    }
}
