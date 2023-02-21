package com.example.rabbitmqconsumer.receiver;

import com.example.rabbitmqconsumer.domain.UserInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;


@Component
public class MyAckReceiver implements ChannelAwareMessageListener {
    private static final Logger logger = LoggerFactory.getLogger(MyAckReceiver.class);

    @Autowired
    ObjectMapper objectMapper;

    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            // 直接用Message类传递数据
            if ("TestDirectQueue".equals(message.getMessageProperties().getConsumerQueue())) {
                logger.info("MyAckReceiver received message from the queue:TestDirectQueue");

                String expiration = message.getMessageProperties().getExpiration();
                String messageId = message.getMessageProperties().getMessageId();
                byte[] messageBody = message.getBody();

                //接收到得message是基本数据类型
                /*String msgStr = new String(messageBody, StandardCharsets.UTF_8);
                logger.info("messageId:{}, msg:{}, expiration:{}", messageId, msgStr, expiration);*/

                //接收到的message是对象
                UserInfo userInfo = objectMapper.readValue(messageBody, UserInfo.class);
                logger.info("messageId:{}, msg:{}, expiration:{}", messageId, userInfo, expiration);
            }else {
                // 用Map传递数据
                String string = message.toString();
                String[] msgArray = string.split("'");
                Map<String, String> msgMap = mapStringToMap(msgArray[1].trim(), 3);

                String msgId = msgMap.get("msgId");
                String msg = msgMap.get("msg");
                String msgDate = msgMap.get("msgDate");

                logger.info("messageId:{}, messageData:{}, createTime:{}", msgId, msg, msgDate);
                if ("fanout.A".equals(message.getMessageProperties().getConsumerQueue())) {
                    logger.info("MyAckReceiver received message from the queue:fanout.A");
                } else {
                    logger.info("MyAckReceiver received message from another queue:{}", message.getMessageProperties().getConsumerQueue());
                }
            }

            //第二个参数，手动确认可以被批处理，当该参数为 true 时，则可以一次性确认 delivery_tag 小于等于传入值的所有消息
            channel.basicAck(deliveryTag, true);
        } catch (Exception e) {
            //第二个参数，设置为true会将该条消息重新放回队列，根据业务逻辑判断什么时候使用
            channel.basicReject(deliveryTag, false);
            e.printStackTrace();
        }
    }

    //{key=value,key=value,key=value} 格式转换成map
    private Map<String, String> mapStringToMap(String str, int entryNum) {
        str = str.substring(1, str.length() - 1);
        String[] strs = str.split(",", entryNum);
        Map<String, String> map = new HashMap<>();
        for (String s : strs) {
            String key = s.split("=")[0].trim();
            String value = s.split("=")[1].trim();
            map.put(key, value);
        }
        return map;
    }
}
