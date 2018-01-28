package com.dachiimp.pstafflist2.Util.MySQL;

import com.dachiimp.pstafflist2.Util.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by George on 15/01/2017. For SL
 */
public class SQLManager {

    static String ip;
    static String port;
    static String database;
    static String username;
    static String password;

    public static void setDB() {
        if (!MySQL.checkConnection()) {
            Logger.severe("Tried to setup database, but no connection was established");
            return;
        }
        try {
            Logger.log("Running database setup check");
            Connection connection = MySQL.getConnection();
            List<String> queries = new ArrayList<>();
            queries.add("CREATE TABLE IF NOT EXISTS `staff` (`uuid` varchar(64),`player` varchar(64),`position` int,`rank` varchar(64),`lastlogin` varchar(64), PRIMARY KEY (`player`))");
            queries.add("CREATE TABLE IF NOT EXISTS `spacers` (`id` varchar(64),`material` varchar(64),`name` varchar(64),`lores` varchar(64), PRIMARY KEY (`id`));");
            for (String query : queries) {
                final PreparedStatement ps = connection.prepareStatement(query);
                ps.executeUpdate();
                ps.close();
            }
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static ResultSet query(String query, HashMap<Integer, Object> values) {
        Connection connection = MySQL.openConnection();
        //<editor-fold desc="<Checking Connection>">
        try {
            if (connection == null || connection.isClosed()) {
                Logger.warn("Error opening connection to run query '" + query + "'");
                return null;
            }
        } catch (SQLException e) {
            Logger.warn("Error opening connection to run query '" + query + "' | Stacktrace:");
            e.printStackTrace();
            return null;
        }
        //</editor-fold>

        try {
            final PreparedStatement preparedStatement = connection.prepareStatement(query);
            final PreparedStatement ps = insertValuesToPS(preparedStatement, values);
            final ResultSet rs = ps.executeQuery();

            return rs;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static PreparedStatement insertValuesToPS(PreparedStatement ps, HashMap<Integer, Object> values) throws SQLException {
        for (int index : values.keySet()) {
            Object obj = values.get(index);
            if (obj instanceof String) {
                ps.setString(index, (String) obj);
            } else if (obj instanceof Integer) {
                ps.setInt(index, (Integer) obj);
            }
            //TODO: Handle if not either string or int
        }

        return ps;
    }

    public static boolean update(String query, HashMap<Integer, Object> values, boolean closeConnection) {
        Connection connection = MySQL.openConnection();
        //<editor-fold desc="<Checking Connection>">
        try {
            if (connection == null || connection.isClosed()) {
                Logger.warn("Error opening connection to run query '" + query + "'");
                return false;
            }
        } catch (SQLException e) {
            Logger.warn("Error opening connection to run query '" + query + "' | Stacktrace:");
            e.printStackTrace();
            return false;
        }
        //</editor-fold>

        try {
            final PreparedStatement preparedStatement = connection.prepareStatement(query);
            final PreparedStatement ps = insertValuesToPS(preparedStatement, values);
            ps.executeUpdate();

            ps.close();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (closeConnection) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    Logger.warn("Error closing connection to database. Stacktrace: ");
                    e.printStackTrace();
                }
            }
        }

        return false;
    }
}
