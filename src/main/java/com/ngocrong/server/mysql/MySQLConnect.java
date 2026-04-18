package com.ngocrong.server.mysql;

import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQLConnect {

    private static Logger logger = Logger.getLogger(MySQLConnect.class);

    private static Connection conn;

    public static Connection getConnection() {
        return conn;
    }

    public static synchronized void create(String host, int port, String database, String user, String pass) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");  // Kiểm tra driver
        } catch (ClassNotFoundException e) {
            
            //System.err.println("Error at 136");
            logger.debug("driver mysql not found!");
            System.exit(0);
        }
        String url = "jdbc:mysql://" + host + ":" + port + "/" + database;
        logger.debug("MySQL connect: " + url);
        try {
            conn = DriverManager.getConnection(url, user, pass);
            logger.debug("successful connection");
        } catch (SQLException e) {
            
            //System.err.println("Error at 135");
            logger.error("failed!", e);
            System.exit(0);
        }
    }

    public static synchronized boolean close() {
        logger.debug("Close connection to database");
        try {
            if (conn != null) {
                conn.close();
            }
            return true;
        } catch (SQLException e) {
            
            //System.err.println("Error at 134");
            logger.error("failed!", e);
            return false;
        }
    }

}
