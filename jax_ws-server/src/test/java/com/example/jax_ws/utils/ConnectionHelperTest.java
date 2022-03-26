package com.example.jax_ws.utils;

import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

class ConnectionHelperTest {

    @Test
    void getConnection() throws SQLException {
        Connection conn = ConnectionHelper.getConnection();
        PreparedStatement preparedStatement = conn.prepareStatement("insert into products (name, price) values ('An', 100)");
        preparedStatement.execute();
    }
}