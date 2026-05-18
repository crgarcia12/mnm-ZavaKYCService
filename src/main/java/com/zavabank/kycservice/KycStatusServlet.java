package com.zavabank.kycservice;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;

public class KycStatusServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String pathInfo = request.getPathInfo();
        if (pathInfo == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"status\":\"ERROR\",\"message\":\"customerId path parameter required.\"}");
            return;
        }

        String[] parts = pathInfo.split("/");
        if (parts.length != 2) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"status\":\"ERROR\",\"message\":\"Expected /api/kyc/status/{customerId}.\"}");
            return;
        }

        int customerId;
        try {
            customerId = Integer.parseInt(parts[1]);
        } catch (NumberFormatException exception) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"status\":\"ERROR\",\"message\":\"customerId must be numeric.\"}");
            return;
        }

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = KycConnectionFactory.openConnection();
            statement = connection.prepareStatement(
                "SELECT TOP 1 Status, Reason, FullName, IdNumber, VerifiedDate " +
                    "FROM KYCVerification WHERE CustomerID = ? ORDER BY VerifiedDate DESC, VerificationID DESC"
            );
            statement.setInt(1, customerId);
            resultSet = statement.executeQuery();

            if (!resultSet.next()) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("{\"customerId\":" + customerId + ",\"status\":\"NOT_FOUND\"}");
                return;
            }

            JSONObject payload = new JSONObject();
            payload.put("customerId", customerId);
            payload.put("status", resultSet.getString("Status"));
            payload.put("reason", resultSet.getString("Reason"));
            payload.put("fullName", resultSet.getString("FullName"));
            payload.put("idNumber", resultSet.getString("IdNumber"));
            Timestamp verifiedDate = resultSet.getTimestamp("VerifiedDate");
            payload.put("verifiedDate", verifiedDate == null ? JSONObject.NULL : verifiedDate.toString());
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(payload.toString());
        } catch (SQLException exception) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"status\":\"ERROR\",\"message\":\"Unable to fetch KYC status.\"}");
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException ignored) {
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException ignored) {
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException ignored) {
                }
            }
        }
    }
}
