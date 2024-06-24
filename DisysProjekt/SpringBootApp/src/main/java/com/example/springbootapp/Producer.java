package com.example.springbootapp;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.context.annotation.Bean;

public class Producer {
    @Bean
    public static void send(String customerId, String queueName, String broker) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(broker);
        factory.setPort(30003);

        try (
                Connection connection = factory.newConnection();
                Channel channel = connection.createChannel()
        ) {
            System.out.println("Connection and Channel created successfully.");
            channel.exchangeDeclare("kwh", "direct");
            channel.queueDeclare(queueName, false, false, false, null);
            channel.queueBind(queueName, "kwh", queueName);
            channel.basicPublish("kwh", queueName, null, customerId.getBytes());
            System.out.println("Sent " + customerId + " to queue: " + queueName);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
}
