package com.example.stationdatacollector;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class Sender {
    public static void sendMessage(String msg, String customerID, String queueName, String brokerUrl) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(brokerUrl);
        factory.setPort(30003);
        msg = customerID + ";" + msg;
        //https://www.rabbitmq.com/tutorials/tutorial-one-java
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.exchangeDeclare("sendID", "direct");
            channel.queueDeclare(queueName, false, false, false, null);
            channel.queueBind(queueName, "sendID", queueName);
            channel.basicPublish("sendID", queueName, null, msg.getBytes());
            System.out.println("Sent '" + msg + "' to queue: " + queueName);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
}