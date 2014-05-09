package com.talient.football.jdbc;

import java.io.*;
import java.util.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.SQLException;
import java.lang.Class;

import com.talient.util.Properties;

public class ConnectionManager
{
    public static final int CONNECTIONS = 10;
    private static Connection connections[] = new Connection[CONNECTIONS];
    private static String connectionUrl = null;
    private static int current = -1;

    public ConnectionManager(String dbUrl)
    {
        connectionUrl = dbUrl;
        if (connectionUrl == null || connectionUrl.equals("")) {
            connectionUrl = null;
        }
        else {
            connectionUrl += "&password=pool";
        }

        // Load the JDBC Driver class
        loadDriver();
    }

    // Load the class for the JDBC driver
    private static void loadDriver()
    {
        try {
            Class.forName("org.gjt.mm.mysql.Driver");
        }
        catch (Exception e) {
            System.err.println("Unable to load mm.mysql JDBC driver.");
            e.printStackTrace();
        }
    }

    // Connect to the database
    public static Connection getConnection() throws Exception
    {
        if (connectionUrl == null) {
            connectionUrl = Properties.getProperty("football.database.url");
            if (connectionUrl == null || connectionUrl.equals("")) {
                connectionUrl = null;
            }
            else {
                connectionUrl += "&password=pool";
            }
        }
        if (connectionUrl == null) {
            System.out.println("football.database.url property not set.");
            throw new Exception("football.database.url property not set.");
        }
        if (current < (CONNECTIONS-1)) {
            current++;
        }
        else {
            current = 0;
        }

        loadDriver();

        if (connections[current] != null) {
            try {
                if (connections[current].isClosed()) {
                    System.err.println("DB Connection[" + current +
                        "] has expired. Reconnecting.");
                    connections[current] = null;
                    return getConnection();
                }
            }
            catch (SQLException e) {
                System.err.println("Exception while checking DB status");
                System.err.println("SQLException: " + e.getMessage());
                System.err.println("SQLState:     " + e.getSQLState());
                System.err.println("VenderError:  " + e.getErrorCode());
                e.printStackTrace();
                connections[current] = null;
                return getConnection();
            }
            return connections[current];
        }
        try {
            connections[current] = DriverManager.getConnection(connectionUrl);
        }
        catch (SQLException e) {
            System.err.println("Exception while getting a connection");
            System.err.println("SQLException: " + e.getMessage());
            System.err.println("SQLState:     " + e.getSQLState());
            System.err.println("VenderError:  " + e.getErrorCode());
            e.printStackTrace();
        }
        return connections[current];
    }


    public static void main(String[] Args)
    {
        ConnectionManager cm =
            new ConnectionManager(Properties.getProperty("football.database.url"));
        try {
            System.out.println("Connection = " + cm.getConnection());
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
