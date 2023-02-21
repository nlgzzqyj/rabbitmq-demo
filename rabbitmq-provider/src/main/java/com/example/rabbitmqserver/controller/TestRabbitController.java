package com.example.rabbitmqserver.controller;

import com.example.rabbitmqserver.controller.dto.UserInfoDto;
import com.example.rabbitmqserver.domain.UserInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RequestMapping("/test/sendMessage")
@RestController
public class TestRabbitController {
    private static final Logger logger = LoggerFactory.getLogger(TestRabbitController.class);

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    ObjectMapper objectMapper;

    @GetMapping("/direct")
    public String testDirect(@RequestParam String msg) {
        Map<String, Object> map = new HashMap<>();
        map.put("msgId", String.valueOf(UUID.randomUUID()));
        map.put("msg", msg);
        map.put("msgDate", new Date());
        //将消息携带绑定键值：TestDirectRoutingKey,发送到交换机TestDirectExchange
        rabbitTemplate.convertAndSend("TestDirectExchange", "TestDirectRoutingKey", map);
        return "ok";
    }

    @GetMapping("/topic/test1")
    public String testTopic1(@RequestParam String msg) {
        Map<String, Object> map = new HashMap<>();
        map.put("msgId", String.valueOf(UUID.randomUUID()));
        map.put("msg", msg);
        map.put("msgDate", new Date());
        rabbitTemplate.convertAndSend("topicExchange", "topic.test1", map);
        return "ok";
    }

    @GetMapping("/topic/test2")
    public String testTopic2(@RequestParam String msg) {
        Map<String, Object> map = new HashMap<>();
        map.put("msgId", String.valueOf(UUID.randomUUID()));
        map.put("msg", msg);
        map.put("msgDate", new Date());
        rabbitTemplate.convertAndSend("topicExchange", "topic.test2", map);
        return "ok";
    }

    @GetMapping("/fanout")
    public String testFanout(@RequestParam String msg) {
        Map<String, Object> map = new HashMap<>();
        map.put("msgId", String.valueOf(UUID.randomUUID()));
        map.put("msg", msg);
        map.put("msgDate", new Date());
        rabbitTemplate.convertAndSend("fanoutExchange", null, map);
        return "ok";
    }

    @GetMapping("/testAck/no-exchange")
    public String testAckNoExchange(@RequestParam String msg) {
        Map<String, Object> map = new HashMap<>();
        map.put("msgId", String.valueOf(UUID.randomUUID()));
        map.put("msg", msg);
        map.put("msgDate", new Date());
        rabbitTemplate.convertAndSend("non-existent-exchange", "TestDirectRoutingKey", map);
        return "ok";
    }

    @GetMapping("/testAck/no-queue")
    public String testAckNoQueue(@RequestParam String msg) {
        Map<String, Object> map = new HashMap<>();
        map.put("msgId", String.valueOf(UUID.randomUUID()));
        map.put("msg", msg);
        map.put("msgDate", new Date());
        rabbitTemplate.convertAndSend("LonelyDirectExchange", "TestDirectRoutingKey", map);
        return "ok";
    }

    @GetMapping("/msg-normal")
    public String testMessage(@RequestParam String msg) {
        logger.info("接收到得数据：{}", msg);
        Message message = MessageBuilder.withBody(msg.getBytes(StandardCharsets.UTF_8)).build();
        message.getMessageProperties().setExpiration("100");
        message.getMessageProperties().setMessageId(String.valueOf(UUID.randomUUID()));
        rabbitTemplate.convertAndSend("TestDirectExchange", "TestDirectRoutingKey", message);
        return "ok";
    }

    @PostMapping("/msg-object")
    public String testMessage(@RequestBody UserInfoDto userInfoDto) {
        logger.info("接收到得数据：{}", userInfoDto);
        UserInfo userInfo = new UserInfo();
        BeanUtils.copyProperties(userInfoDto, userInfo);
        try {
            Message message = MessageBuilder.withBody(objectMapper.writeValueAsBytes(userInfo)).build();
            message.getMessageProperties().setExpiration("100");
            message.getMessageProperties().setMessageId(String.valueOf(UUID.randomUUID()));
            rabbitTemplate.convertAndSend("TestDirectExchange", "TestDirectRoutingKey", message);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return "ok";
    }
}
