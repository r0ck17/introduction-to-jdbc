package by.javaguru.util;

import by.javaguru.entity.Aircraft;
import by.javaguru.entity.Airport;
import by.javaguru.entity.Flight;
import by.javaguru.entity.Seat;
import by.javaguru.entity.Ticket;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Slf4j
@UtilityClass
public class ConnectionManager {
    private static final Logger logger = LoggerFactory.getLogger(ConnectionManager.class);
    private static final String URL_KEY = "db.url";
    private static final String USERNAME_KEY = "db.username";
    private static final String PASSWORD_KEY = "db.password";
    private static volatile Connection connection;
    private static SessionFactory sessionFactory;

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

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            log.info("Configure session factory");
            sessionFactory = new Configuration()
                    .configure()
                    .addAnnotatedClass(Aircraft.class)
                    .addAnnotatedClass(Airport.class)
                    .addAnnotatedClass(Flight.class)
                    .addAnnotatedClass(Seat.class)
                    .addAnnotatedClass(Ticket.class)
                    .setPhysicalNamingStrategy(new CamelCaseToUnderscoresNamingStrategy())
                    .buildSessionFactory();
            log.info("Session factory configured");
        }

        return sessionFactory;
    }

    public static void closeSessionFactory() {
        logger.info("Trying to close session factory");
        sessionFactory.close();
        logger.info("Session factory closed");
    }

    public static void close() {
        try {
            logger.info("Trying to close connection");
            connection.close();
            logger.info("Connection closed");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
