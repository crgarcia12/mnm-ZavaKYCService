package com.zavabank.kycservice;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

public class KycBootstrapServlet extends HttpServlet {
    @Override
    public void init() throws ServletException {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = KycConnectionFactory.openConnection();
            statement = connection.prepareStatement(
                "IF OBJECT_ID('KYCWatchlist', 'U') IS NULL " +
                    "CREATE TABLE KYCWatchlist (" +
                    "WatchlistID INT IDENTITY(1,1) PRIMARY KEY, " +
                    "FullName NVARCHAR(200) NOT NULL, " +
                    "Source NVARCHAR(100) NULL, " +
                    "CreatedDate DATETIME DEFAULT GETDATE()" +
                    "); " +
                    "IF OBJECT_ID('KYCVerification', 'U') IS NULL " +
                    "CREATE TABLE KYCVerification (" +
                    "VerificationID BIGINT IDENTITY(1,1) PRIMARY KEY, " +
                    "CustomerID INT NOT NULL, " +
                    "FullName NVARCHAR(200) NOT NULL, " +
                    "IdNumber NVARCHAR(100) NULL, " +
                    "Status NVARCHAR(20) NOT NULL, " +
                    "Reason NVARCHAR(500) NULL, " +
                    "VerifiedDate DATETIME DEFAULT GETDATE()" +
                    "); " +
                    "IF NOT EXISTS (SELECT 1 FROM KYCWatchlist) " +
                    "INSERT INTO KYCWatchlist (FullName, Source) VALUES ('John Alias', 'OFAC-SAMPLE');"
            );
            statement.execute();
        } catch (SQLException exception) {
            throw new ServletException("KYC bootstrap failed.", exception);
        } finally {
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
