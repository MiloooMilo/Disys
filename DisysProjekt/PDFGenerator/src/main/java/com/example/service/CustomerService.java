package com.example.service;
import com.example.model.Customer;

import java.sql.*;

public class CustomerService {

    private static Connection connect(String dburl) throws SQLException {
        String connectionString="jdbc:postgresql://"+ dburl + "/customerdb";
        return DriverManager.getConnection(connectionString, "postgres", "postgres");
    }


    public static Customer getCustomerData(String customerId, String dburl) {
        Customer customer = null;
        try ( Connection conn = connect(dburl) ) {
            String sql = "SELECT id, first_name, last_name FROM customer WHERE id = " + customerId +";";
            ResultSet data = getResultSet(conn, sql);
            customer = convertResultSet(data);
        }catch (SQLException e) {
            e.printStackTrace();
        }

        return customer;
    }

    private static ResultSet getResultSet(Connection conn, String query) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement(query);
        return stmt.executeQuery();
    }

    private static Customer convertResultSet(ResultSet rs) throws SQLException {
        Customer customer = null;
        while (rs.next()) {
            customer = new Customer(
                    rs.getInt(1),
                    rs.getString(2),
                    rs.getString(3)
            );

        }
        return customer;
    }
}