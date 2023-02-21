package com.example.rabbitmqconsumer.config;

import com.example.rabbitmqconsumer.receiver.MyAckReceiver;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MessageListenerConfig {
    @Autowired
    private CachingConnectionFactory connectionFactory;

    @Autowired
    private MyAckReceiver myAckReceiver;

    @Bean
    public SimpleMessageListenerContainer simpleMessageListenerContainer(){
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
        container.setConcurrentConsumers(1);
        container.setMaxConcurrentConsumers(1);
        // RabbitMQ默认是自动确认，这里改为手动确认消息
        container.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        // 设置队列
//        container.setQueueNames("TestDirectQueue");
        // 如果需要设置多个队列：
        container.setQueueNames("TestDirectQueue", "fanout.A", "fanout.B", "fanout.C");
        //消息接收处理类
        container.setMessageListener(myAckReceiver);

        return container;
    }
}
