package gmail.alexejkrawez.app.entities;

import gmail.alexejkrawez.app.model.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDAO extends ConnectionDAO {

    private static final String ADD_USER = "INSERT INTO users (email, login, password) VALUES (?, ?, ?);";

    private static final String SELECT_BY_EMAIL_LOGIN_PASSWORD = "SELECT user_id, email, login, password " +
            "FROM users " +
            "WHERE email = ? AND login = ? AND password = ?;";
    private static final String SELECT_BY_LOGIN_PASSWORD = "SELECT user_id, email, login, password " +
            "FROM users " +
            "WHERE login = ? AND password = ? ";

    private static final String SELECT_EMAIL = "SELECT true AS email FROM users WHERE email = ? LIMIT 1;";
    private static final String SELECT_LOGIN = "SELECT true AS login FROM users WHERE login = ? LIMIT 1;";


    synchronized public static int createUser(String email, String login, String password) {
        String validEmail = isEmailExist(email);
        String validLogin = isLoginExist(login);

        if (validEmail != null & validLogin != null) {
            User user = selectByEmailLoginPassword(email, login, password);
            if (user != null) {
                logger.warn("User " + login + " is already exist.");
                return 1;
            }

        } else if (validEmail != null) {
            logger.warn("ValidEmail " + email + " is already exist.");
            return 2;
        } else if (validLogin != null) {
            logger.warn("User " + login + " is already exist.");
            return 3;
        }

        try (PreparedStatement ps = getConnection().prepareStatement(ADD_USER)) {
            ps.setString(1, email);
            ps.setString(2, login);
            ps.setString(3, password);
            ps.executeUpdate();
            logger.info("User " + login + " is created.");
        } catch (SQLException e) {
            logger.error(email + ", " + login + ": ");
            logger.error(e.getMessage(), e);
        }

        return 0;
    }

    synchronized public static User getUser(String login, String password) {
        User user = selectByLoginPassword(login, password);
        if (user == null) {
            logger.warn("User " + login + " is not exist.");
            return null;
        }

        return user;
    }

    synchronized public static String isEmailExist(String email) {
        String dbEmail = null;
        try (PreparedStatement ps = getConnection().prepareStatement(SELECT_EMAIL)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            while(rs.next()) {
                dbEmail = rs.getString("email");
            }
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
        }
        return dbEmail;
    }

    synchronized public static String isLoginExist(String login) {
        String dbLogin = null;
        try (PreparedStatement ps = getConnection().prepareStatement(SELECT_LOGIN)) {
            ps.setString(1, login);
            ResultSet rs = ps.executeQuery();

            while(rs.next()) {
                dbLogin = rs.getString("login");
            }
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
        }
        return dbLogin;
    }


    synchronized private static User selectByLoginPassword(String login, String password) {

        List<User> usersList = new ArrayList<>();
        try (PreparedStatement ps = getConnection().prepareStatement(SELECT_BY_LOGIN_PASSWORD)) {
            ps.setString(1, login);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();
            fillList(usersList, rs);
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
        }

        if (!usersList.isEmpty()) {
            return usersList.get(0);
        }

        return null;
    }

    synchronized private static User selectByEmailLoginPassword(String email, String login, String password) {

        List<User> usersList = new ArrayList<>();
        try (PreparedStatement ps = getConnection().prepareStatement(SELECT_BY_EMAIL_LOGIN_PASSWORD)) {
            ps.setString(1, email);
            ps.setString(2, login);
            ps.setString(3, password);

            ResultSet rs = ps.executeQuery();
            fillList(usersList, rs);
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
        }

        if (!usersList.isEmpty()) {
            return usersList.get(0);
        }

        return null;
    }

    synchronized private static void fillList(List<User> usersList, ResultSet rs) throws SQLException {
        while(rs.next()) {
            int dbUserId = rs.getInt("user_id");
            String dbEmail = rs.getString("email");
            String dbLogin = rs.getString("login");
            String dbPassword = rs.getString("password");
            User user = new User(dbUserId, dbEmail, dbLogin, dbPassword);
            usersList.add(user);
        }
    }

}