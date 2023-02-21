package com.example.rabbitmqserver.config;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/*
* provider消息确认回调机制：
* 推送消息存在四种情况：
* 1、消息推送到server，但是在server里找不到交换机
* 2、消息推送到server，找到交换机了，但是没找到队列
* 3、消息推送到sever，交换机和队列啥都没找到
* 4、消息推送成功
* */
@Configuration
public class RabbitConfig {
    private static final Logger logger = LoggerFactory.getLogger(RabbitConfig.class);

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory){
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setConnectionFactory(connectionFactory);
        //设置开启Mandatory,才能触发回调函数,无论消息推送结果怎么样都强制调用回调函数
        rabbitTemplate.setMandatory(true);

        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            @Override
            public void confirm(CorrelationData correlationData, boolean ack, String cause) {
                logger.info("ConfirmCallback correlationData：{}", correlationData);
                logger.info("ConfirmCallback ack：{}", ack);
                logger.info("ConfirmCallback cause：{}", cause);
            }
        });

        rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {
            @Override
            public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
                logger.info("ReturnCallback message：{}", message);
                logger.info("ReturnCallback replyCode：{}", replyCode);
                logger.info("ReturnCallback replyText：{}", replyText);
                logger.info("ReturnCallback exchange：{}", exchange);
                logger.info("ReturnCallback routingKey：{}", routingKey);
            }
        });

        return rabbitTemplate;
    }
}
