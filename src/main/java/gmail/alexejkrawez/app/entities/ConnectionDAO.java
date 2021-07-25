package gmail.alexejkrawez.app.entities;

import gmail.alexejkrawez.db.DBConnector;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.sql.Connection;

public class ConnectionDAO {

    public static final Logger logger = LogManager.getLogger(ConnectionDAO.class);
    static Connection getConnection() {
        return DBConnector.getInstance().getConnection();
    } // TODO насчёт статика не уверен
}
