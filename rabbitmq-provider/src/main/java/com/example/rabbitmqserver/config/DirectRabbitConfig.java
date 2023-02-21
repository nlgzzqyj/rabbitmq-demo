package com.example.rabbitmqserver.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class DirectRabbitConfig {
    /*
        durable:是否持久化,默认是false,持久化队列：会被存储在磁盘上，当消息代理重启时仍然存在，暂存队列：仅当前连接有效
        exclusive:默认也是false，只能被当前创建的连接使用，而且当连接关闭后队列即被删除。此参考优先级高于durable
        autoDelete:是否自动删除，当没有生产者或者消费者使用此队列，该队列会自动删除。
    */

    // 队列：TestDirectQueue
    @Bean
    public Queue TestDirectQueue() {
        return new Queue("TestDirectQueue", true);
    }

    // Direct交换机：TestDirectExchange
    @Bean
    public DirectExchange TestDirectExchange() {
        return new DirectExchange("TestDirectExchange", true, false);
    }

    // 绑定：将队列和交换机绑定, 并设置路由键：TestDirectRoutingKey
    @Bean
    Binding bindingDirect() {
        return BindingBuilder.bind(TestDirectQueue()).to(TestDirectExchange()).with("TestDirectRoutingKey");
    }

    @Bean
    DirectExchange LonelyDirectExchange() {
        return new DirectExchange("LonelyDirectExchange");
    }
}
