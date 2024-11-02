package dataaccess;

import model.UserData;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

public class SqlUserDAO implements UserDAO {

    public SqlUserDAO() {
        String statement = """            
                    CREATE TABLE if NOT EXISTS UserTable (
                                    `username` VARCHAR(255) NOT NULL,
                                    `password` VARCHAR(255) NOT NULL,
                                    `email` VARCHAR(255) NOT NULL,
                                    PRIMARY KEY (username)
                                    )""";

        try { DatabaseManager.createDatabase(); } catch (DataAccessException ex) {
            throw new RuntimeException(ex);
        }
        try (var connection = DatabaseManager.getConnection()) {

            try (var prepareStatement = connection.prepareStatement(statement)) {

                prepareStatement.executeUpdate();

            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean containsUser(String username) {
        var toReturn = new ArrayList<>();
        try { DatabaseManager.createDatabase(); } catch (DataAccessException ex) {
            throw new RuntimeException(ex);
        }
        try (var connection = DatabaseManager.getConnection()) {
            String statement = "SELECT username FROM UserTable";
            try (var prepareStatement = connection.prepareStatement(statement)) {
                ResultSet rs = prepareStatement.executeQuery();
                while (rs.next()) {
                    toReturn.add(rs.getString("username"));
                }
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }

        return toReturn.contains(username);
    }

    @Override
    public UserData getUser(String username) {
        String statement = "SELECT * FROM UserTable WHERE username=?";

        UserData toReturn = null;
        try { DatabaseManager.createDatabase(); } catch (DataAccessException ex) {
            throw new RuntimeException(ex);
        }
        try (var connection = DatabaseManager.getConnection()) {

            try (var prepareStatement = connection.prepareStatement(statement)) {
                prepareStatement.setString(1, username);
                ResultSet rs = prepareStatement.executeQuery();
                if (rs.next()) {
                    toReturn = new UserData(rs.getString("username"),
                                            rs.getString("password"),
                                            rs.getString("email"));
                }
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }

        return toReturn;
    }

    @Override
    public void addUser(UserData userData) {
        String statement = "INSERT INTO UserTable (username, password, email) VALUES (?,?,?)";

        try { DatabaseManager.createDatabase(); } catch (DataAccessException ex) {
            throw new RuntimeException(ex);
        }
        try (var connection = DatabaseManager.getConnection()) {

            try (var prepareStatement = connection.prepareStatement(statement)) {
                prepareStatement.setString(1, userData.username());
                prepareStatement.setString(2, userData.password());
                prepareStatement.setString(3, userData.email());
                prepareStatement.executeUpdate();

            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteAll() {
        try { DatabaseManager.createDatabase(); } catch (DataAccessException ex) {
            throw new RuntimeException(ex);
        }
        try (var connection = DatabaseManager.getConnection()) {
            String statement = "TRUNCATE UserTable";
            try (var prepareStatement = connection.prepareStatement(statement)) {
                prepareStatement.executeUpdate();
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
