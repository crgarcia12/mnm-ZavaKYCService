package com.zavabank.kycservice;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;

public class KycVerifyServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String body = readBody(request);
        JSONObject payload;
        try {
            payload = new JSONObject(body);
        } catch (Exception exception) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"status\":\"ERROR\",\"message\":\"Invalid JSON payload.\"}");
            return;
        }

        int customerId = payload.optInt("customerId", 0);
        String fullName = payload.optString("fullName", "").trim();
        String idNumber = payload.optString("idNumber", "").trim();
        if (customerId <= 0 || fullName.length() == 0) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"status\":\"ERROR\",\"message\":\"customerId and fullName are required.\"}");
            return;
        }

        Connection connection = null;
        try {
            connection = KycConnectionFactory.openConnection();
            String status = existsInWatchlist(connection, fullName) ? "REVIEW" : "PASS";
            String reason = "REVIEW".equals(status) ? "Name matched watchlist." : "No watchlist match.";
            insertVerification(connection, customerId, fullName, idNumber, status, reason);

            JSONObject result = new JSONObject();
            result.put("customerId", customerId);
            result.put("status", status);
            result.put("reason", reason);
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(result.toString());
        } catch (SQLException exception) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"status\":\"ERROR\",\"message\":\"Unable to complete KYC verification.\"}");
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException ignored) {
                }
            }
        }
    }

    private String readBody(HttpServletRequest request) throws IOException {
        BufferedReader reader = request.getReader();
        StringBuilder body = new StringBuilder();
        String line = reader.readLine();
        while (line != null) {
            body.append(line);
            line = reader.readLine();
        }
        return body.toString();
    }

    private boolean existsInWatchlist(Connection connection, String fullName) throws SQLException {
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement("SELECT TOP 1 WatchlistID FROM KYCWatchlist WHERE UPPER(FullName) = UPPER(?)");
            statement.setString(1, fullName);
            resultSet = statement.executeQuery();
            return resultSet.next();
        } finally {
            if (resultSet != null) {
                resultSet.close();
            }
            if (statement != null) {
                statement.close();
            }
        }
    }

    private void insertVerification(
        Connection connection,
        int customerId,
        String fullName,
        String idNumber,
        String status,
        String reason
    ) throws SQLException {
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(
                "INSERT INTO KYCVerification (CustomerID, FullName, IdNumber, Status, Reason, VerifiedDate) VALUES (?, ?, ?, ?, ?, GETDATE())"
            );
            statement.setInt(1, customerId);
            statement.setString(2, fullName);
            statement.setString(3, idNumber);
            statement.setString(4, status);
            statement.setString(5, reason);
            statement.executeUpdate();
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
    }
}
