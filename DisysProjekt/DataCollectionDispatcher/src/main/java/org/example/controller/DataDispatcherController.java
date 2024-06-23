package org.example.controller;


import org.example.communication.Collector;
import org.example.communication.Sender;
import org.example.model.Station;
import org.example.service.Service;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class DataDispatcherController {

    private final Service dataCollectionService;
    private String broker;

    public DataDispatcherController() {
        this.dataCollectionService = new Service();
    }

    public void run(String queueName, String brokerUrl) throws IOException, TimeoutException {
        this.broker = brokerUrl;
        Collector.listenForMessages(queueName, brokerUrl, this);
    }

    public void startDispatching(String customerId) throws SQLException {
        ArrayList<Station> stations = dataCollectionService.getStations();
        Sender.sendMessage("start", customerId, "RECEIVER", broker);
        for (Station station : stations) {
            Sender.sendMessage(station.getDb_url(), customerId, "COLLECTOR", broker);
        }
        Sender.sendMessage("end", customerId, "COLLECTOR", broker);

    }
}