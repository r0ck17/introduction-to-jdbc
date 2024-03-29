package by.javaguru.util;

import lombok.experimental.UtilityClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@UtilityClass
public class ConnectionManager {
    private static final Logger logger = LoggerFactory.getLogger(ConnectionManager.class);
    private static final String URL_KEY = "db.url";
    private static final String USERNAME_KEY = "db.username";
    private static final String PASSWORD_KEY = "db.password";
    private static volatile Connection connection;

    public static Connection open() {
        try {
            if (connection == null) {
                logger.info("Trying to open new connection.");
                synchronized (ConnectionManager.class) {
                    if (connection == null) {
                        connection = DriverManager.getConnection(
                                PropertiesUtil.get(URL_KEY),
                                PropertiesUtil.get(USERNAME_KEY),
                                PropertiesUtil.get(PASSWORD_KEY));
                        connection.setSchema("flights");
                        logger.info("Connection to database was opened. Schema: {}", connection.getSchema());
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
            logger.info("Trying to close connection.");
            connection.close();
            logger.info("Connection closed");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
