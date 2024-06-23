package com.example.stationdatacollector;


import org.example.service.Service;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class DataCollectionService {
    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Failed to load PostgreSQL driver", e);
        }
    }

    public static Connection connect(String stationDbUrl) throws SQLException {
        // Korrektur der URL: Füge / am Ende hinzu und gebe den Datenbanknamen an
        String dbUrl = "jdbc:postgresql://" + stationDbUrl + "/stationdb";
        return DriverManager.getConnection(dbUrl, "postgres", "postgres");
    }

    public List<String> collectDataFromStationDb(String stationDbUrl, String customerId) throws SQLException {
        List<String> collectedData = new ArrayList<>();

        int customerIdInt = Integer.parseInt(customerId);
        if (customerIdInt >= 4) {
            throw new IllegalArgumentException("Invalid customer ID: " + customerId + ". Customer ID must be less than 4.");
        }

        try (Connection conn = connect(stationDbUrl)) {
            String query = "SELECT kwh FROM charge WHERE customer_id = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setInt(1, customerIdInt);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    String kwh = resultSet.getString("kwh");
                    collectedData.add(kwh);
                }
            }
        }

        return collectedData;
    }
}