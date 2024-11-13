package dataaccess;

import model.AuthData;

import java.sql.*;
import java.util.ArrayList;

public class SqlAuthDAO implements AuthDAO {

    public SqlAuthDAO() {
        String statement = """            
                    CREATE TABLE if NOT EXISTS AuthTable (
                                    username VARCHAR(255) NOT NULL,
                                    authToken VARCHAR(255) NOT NULL,
                                    PRIMARY KEY (authToken)
                                    )""";

        deleteAll();

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
    public boolean containsAuthToken(String authToken) {
        var toReturn = new ArrayList<>();
        try { DatabaseManager.createDatabase(); } catch (DataAccessException ex) {
            throw new RuntimeException(ex);
        }
        try (var connection = DatabaseManager.getConnection()) {
            String statement = "SELECT authToken FROM AuthTable";
            try (var prepareStatement = connection.prepareStatement(statement)) {
                ResultSet rs = prepareStatement.executeQuery();
                while (rs.next()) {
                    toReturn.add(rs.getString("authToken"));
                }
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }

        return toReturn.contains(authToken);
    }


    @Override
    public void addAuth(AuthData authData) {
        try { DatabaseManager.createDatabase(); } catch (DataAccessException ex) {
            throw new RuntimeException(ex);
        }
        try (var connection = DatabaseManager.getConnection()) {
            String statement = "INSERT INTO AuthTable (username, authToken) VALUES(?, ?)";
            try (var prepareStatement = connection.prepareStatement(statement)) {
                prepareStatement.setString(1, authData.username());
                prepareStatement.setString(2, authData.authToken());
                prepareStatement.executeUpdate();
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteAuth(String authToken) {
        try { DatabaseManager.createDatabase(); } catch (DataAccessException ex) {
            throw new RuntimeException(ex);
        }
        try (var connection = DatabaseManager.getConnection()) {
            String statement = "DELETE FROM AuthTable WHERE authToken=?";
            try (var prepareStatement = connection.prepareStatement(statement)) {
                prepareStatement.setString(1, authToken);
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
            String statement = "TRUNCATE AuthTable";
            try (var prepareStatement = connection.prepareStatement(statement)) {
                prepareStatement.executeUpdate();
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public AuthData getAuth(String authToken) {
        String statement = "SELECT username, authToken FROM AuthTable WHERE authToken=?";

        AuthData toReturn = null;
        try { DatabaseManager.createDatabase(); } catch (DataAccessException ex) {
            throw new RuntimeException(ex);
        }
        try (var connection = DatabaseManager.getConnection()) {

            try (var prepareStatement = connection.prepareStatement(statement)) {
                prepareStatement.setString(1, authToken);
                ResultSet rs = prepareStatement.executeQuery();
                if (rs.next()) {
                    toReturn = new AuthData(rs.getString("username"), authToken);
                }
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }

        return toReturn;
    }
}
