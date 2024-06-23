package org.example.service;
import org.example.model.Station;

import java.sql.*;
import java.util.ArrayList;

public class Service {

    private static Connection connect() throws SQLException {
        String connectionString="jdbc:postgresql://localhost:30002/stationdb";
        return DriverManager.getConnection(connectionString, "postgres", "postgres");
    }

    public static ArrayList<Station> getStations() throws SQLException {
        ArrayList<Station> stations = new ArrayList<>();

        try ( Connection conn = connect() ) {
            String query = "SELECT id, db_url, lat, lng FROM station;";
            ResultSet resultSet = getResultSet(conn, query);
            stations = convertResultSet(resultSet);
        }

        return stations;
    }

    private static ResultSet getResultSet(Connection conn, String query) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement(query);
        return stmt.executeQuery();
    }

    private static ArrayList<Station> convertResultSet(ResultSet rs) throws SQLException {
        ArrayList<Station> stations = new ArrayList<>();
        while (rs.next()) {
            Station station = new Station(
                    rs.getInt(1),
                    rs.getString(2),
                    rs.getFloat(3),
                    rs.getFloat(4)
            );
            stations.add(station);
        }
        return stations;
    }
}