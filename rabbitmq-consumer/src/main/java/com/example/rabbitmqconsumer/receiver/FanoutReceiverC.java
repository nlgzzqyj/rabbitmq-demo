package com.example.rabbitmqconsumer.receiver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RabbitListener(queues = "fanout.C")
public class FanoutReceiverC {

    private static final Logger logger = LoggerFactory.getLogger(FanoutReceiverC.class);

    @RabbitHandler
    public void process(Map<String, Object> map){
        logger.info("FanoutReceiverC:{}", map);
    }
}
