package com.dachiimp.pstafflist2.Util.MySQL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by George on 15/01/2017. For SL
 */
public class MySQL {
    private static Connection connection;

    private static String user;
    private static String database;
    private static String password;
    private static String port;
    private static String hostname;

    public MySQL(String hostname, String port, String database, String username, String password) {
        this.hostname = hostname;
        this.port = port;
        this.database = database;
        this.user = username;
        this.password = password;
    }

    public static Connection openConnection() {
        if (checkConnection()) {
            return connection;
        }
        try {
            String connectionURL = "jdbc:mysql://" + hostname + ":" + port + "/" + database/* + "?autoReconnect=true"*/;
            /*Logger.log("Connection url:");
            Logger.log(connectionURL);
            Logger.log("Password: " + password + " | Username: " + user);*/
            connection = DriverManager.getConnection(connectionURL, user, password);
        } catch (SQLException e) {
            System.out.println("Error connecting to database. Stacktrace:");
            e.printStackTrace();
        }
        return connection;
    }

    public static boolean checkConnection() {
        if (connection != null) {
            try {
                return !connection.isClosed();
            } catch (SQLException e) {
                return false;
            }
        }
        return false;
    }

    public static Connection getConnection() {
        return connection;
    }

    public static void closeConnection() {
        try {
            if (connection != null)
                connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}