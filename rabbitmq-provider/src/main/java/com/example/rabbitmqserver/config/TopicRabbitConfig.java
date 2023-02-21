package com.example.rabbitmqserver.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TopicRabbitConfig {

    @Bean
    public Queue firstQueue(){
        return new Queue("test1");
    }

    @Bean
    public Queue secondQueue(){
        return new Queue("test2");
    }

    @Bean
    TopicExchange topicExchange(){
        return new TopicExchange("topicExchange");
    }

    @Bean
    Binding binding(){
        //队列test1与topicExchange绑定，并设置路由键为topic.test1
        return BindingBuilder.bind(firstQueue()).to(topicExchange()).with("topic.test1");
    }

    @Bean
    Binding binding2(){
        /*
        * * (星号) 用来表示一个单词 (必须出现的)
        * # (井号) 用来表示任意数量（零个或多个）单词
        * */

        //队列test2与topicExchange绑定，并设置路由键为topic.#，这样只要是消息携带的路由键是以topic.开头,都会分发到该队列
        return BindingBuilder.bind(secondQueue()).to(topicExchange()).with("topic.#");
    }

}
