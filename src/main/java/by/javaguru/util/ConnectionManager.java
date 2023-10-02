package by.javaguru.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionManager {
    private static final String URL_KEY = "db.url";
    private static final String USERNAME_KEY = "db.username";
    private static final String PASSWORD_KEY = "db.password";
    private static volatile Connection connection;

    private ConnectionManager() {}

    public static Connection open() {
        try {
            if (connection == null) {
                synchronized (ConnectionManager.class) {
                    if (connection == null) {
                        connection = DriverManager.getConnection(
                                PropertiesUtil.get(URL_KEY),
                                PropertiesUtil.get(USERNAME_KEY),
                                PropertiesUtil.get(PASSWORD_KEY));
                    }
                }
            }
            return connection;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void close() {
        try {
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}