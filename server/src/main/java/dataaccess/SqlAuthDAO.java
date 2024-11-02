package dataaccess;

import model.AuthData;

import java.sql.*;

public class SqlAuthDAO implements AuthDAO {

    public SqlAuthDAO() {
        String statement = """            
                    CREATE TABLE if NOT EXISTS AuthTable (
                                    username VARCHAR(255) NOT NULL,
                                    authToken VARCHAR(255) NOT NULL,
                                    PRIMARY KEY (authToken)
                                    )""";
        executeSqlUpdate(statement);

    }

    public static void executeSqlUpdate(String statement) {
        // FIXME:: Safest way is to use prepareStatement with ?'s, then use set___ methods
        // FIXME:: Easiest way to potentially do that is to do everything separately in their own methods
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
        String statement = String.format("""
                SELECT
                  EXISTS (
                    SELECT 1
                    FROM AuthTable
                    WHERE authToken = %s
                  ) AS token_exists""", authToken);

        return verifyQuery(statement);
    }

    static boolean verifyQuery(String statement) {
        boolean tokenExists = false;
        try { DatabaseManager.createDatabase(); } catch (DataAccessException ex) {
            throw new RuntimeException(ex);
        }
        try (var connection = DatabaseManager.getConnection()) {

            try (var prepareStatement = connection.prepareStatement(statement)) {
                ResultSet rs = prepareStatement.executeQuery();
                if (rs.next()) {
                    tokenExists = rs.getBoolean("token_exists");
                }
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }

        return tokenExists;
    }

    @Override
    public void addAuth(AuthData authData) {
        String statement = String.format("INSERT INTO AuthTable (username, authToken) VALUES(%s, %s)",
                                          authData.username(), authData.authToken());

        executeSqlUpdate(statement);
    }

    @Override
    public void deleteAuth(String authToken) {
        String statement = String.format("DELETE FROM AuthTable WHERE authToken=%s", authToken);

        executeSqlUpdate(statement);
    }

    @Override
    public void deleteAll() {
        String statement = "TRUNCATE AuthTable";

        executeSqlUpdate(statement);
    }

    @Override
    public AuthData getAuth(String authToken) {
        String statement = String.format("SELECT username, authToken FROM AuthTable WHERE authToken=%s", authToken);

        AuthData toReturn = null;
        try { DatabaseManager.createDatabase(); } catch (DataAccessException ex) {
            throw new RuntimeException(ex);
        }
        try (var connection = DatabaseManager.getConnection()) {

            try (var prepareStatement = connection.prepareStatement(statement)) {
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
