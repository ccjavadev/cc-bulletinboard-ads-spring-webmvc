package com.sap.bulletinboard.ads.services;

import java.nio.charset.Charset;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.core.Queue;
import org.springframework.stereotype.Component;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.sap.hcp.cf.logging.common.LogContext;

@Component // defines a Spring Bean with name "statisticsServiceClient"
public class StatisticsServiceClient {
    private static final Charset CHARSET_UTF8 = Charset.forName("UTF-8");
    private static final String ROUTING_KEY = "statistics.adIsShown";

    private Logger logger = LoggerFactory.getLogger(getClass());

    private AmqpTemplate amqpTemplate;

    @Inject
    public StatisticsServiceClient(AmqpAdmin amqpAdmin, AmqpTemplate amqpTemplate) {
        amqpAdmin.declareQueue(new Queue(ROUTING_KEY)); // creates queue, if not existing
        this.amqpTemplate = amqpTemplate;
    }

    public void advertisementIsShown(long id) {
        new IncrementCounterCommand(id).queue(); // queue calls the run() asynchronously
    }

    private class IncrementCounterCommand extends HystrixCommand<Void> {
        private final String correlationId;
        private long id;

        IncrementCounterCommand(long id) {
            super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(StatisticsServiceClient.class.getName()))
                    .andCommandKey(HystrixCommandKey.Factory.asKey(ROUTING_KEY)));
            this.id = id;
            this.correlationId = LogContext.getCorrelationId();
        }

        @Override
        protected Void run() throws Exception {
            LogContext.initializeContext(this.correlationId);

            String message = String.valueOf(id);
            logger.info("sending message '{}' for routing key '{}'", message, ROUTING_KEY);
            amqpTemplate.send(ROUTING_KEY, getMessage(message));
            return null;
        }

        @Override
        protected Void getFallback() {
            logger.warn("Failure to send message to statistics service");
            return null;
        }

        private Message getMessage(String messageString) {
            MessageProperties messageProperties = new MessageProperties();
            messageProperties.setCorrelationIdString(LogContext.getCorrelationId());
            return new Message(toByteArray(messageString), messageProperties);
        }
    }

    private byte[] toByteArray(String string) {
        return string.getBytes(CHARSET_UTF8);
    }
}