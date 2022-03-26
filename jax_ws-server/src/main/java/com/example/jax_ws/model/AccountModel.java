package com.example.jax_ws.model;

import com.example.jax_ws.entity.Account;
import com.example.jax_ws.utils.ConnectionHelper;

import java.sql.*;

public class AccountModel {
    private Connection conn;

    public AccountModel() {
        conn = ConnectionHelper.getConnection();
    }

    public Account findById(int id) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("select * from accounts where id = ?");
        stmt.setInt(1, id);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            String email = rs.getString("email");
            String password = rs.getString("password");
            String firstName = rs.getString("firstName");
            String lastName = rs.getString("lastName");
            return new Account(email, password, firstName, lastName);
        }
        return null;
    }

    public Account findByEmail(String email) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("select * from accounts where email = ?");
        stmt.setString(1, email);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            String password = rs.getString("password");
            String firstName = rs.getString("firstName");
            String lastName = rs.getString("lastName");
            return new Account(email, password, firstName, lastName);
        }
        return null;
    }

    public Account save(Account account) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("insert into accounts (email, password, firstName, lastName) values (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
        stmt.setString(1, account.getEmail());
        stmt.setString(2, account.getPassword());
        stmt.setString(3, account.getFirstName());
        stmt.setString(4, account.getLastName());
        int affectedRows = stmt.executeUpdate();
        if (affectedRows > 0) {
            ResultSet resultSetGeneratedKeys = stmt.getGeneratedKeys();
            if (resultSetGeneratedKeys.next()) {
                int id = resultSetGeneratedKeys.getInt(1);
                account.setId(id);
                return account;
            }
        }

        return null;
    }
}
