package dataaccess;

import model.UserData;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SqlUserDAO implements UserDAO {

    public SqlUserDAO() {
        String statement = """            
                    CREATE TABLE if NOT EXISTS UserTable (
                                    username VARCHAR(255) NOT NULL,
                                    password VARCHAR(255) NOT NULL,
                                    email VARCHAR(255) NOT NULL,
                                    PRIMARY KEY (username)
                                    )""";
        SqlAuthDAO.executeSqlUpdate(statement);
    }

    @Override
    public boolean containsUser(String username) {
        String statement = String.format("""
                SELECT
                  EXISTS (
                    SELECT 1
                    FROM UserTable
                    WHERE username = %s
                  ) AS token_exists""", username);

        return SqlAuthDAO.verifyQuery(statement);
    }

    @Override
    public UserData getUser(String username) {
        String statement = String.format("SELECT * FROM UserData WHERE username=%s", username);

        UserData toReturn = null;
        try { DatabaseManager.createDatabase(); } catch (DataAccessException ex) {
            throw new RuntimeException(ex);
        }
        try (var connection = DatabaseManager.getConnection()) {

            try (var prepareStatement = connection.prepareStatement(statement)) {
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
        String statement = String.format("INSERT INTO UserTable (username, password, email) VALUES(%s, %s, %s)",
                userData.username(), userData.password(), userData.email());

        SqlAuthDAO.executeSqlUpdate(statement);
    }

    @Override
    public void deleteAll() {
        String statement = "TRUNCATE UserTable";

        SqlAuthDAO.executeSqlUpdate(statement);
    }
}
