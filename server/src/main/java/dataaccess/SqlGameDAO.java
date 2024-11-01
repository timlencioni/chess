package dataaccess;

import chess.ChessGame;
import model.GameData;
import model.JoinGameData;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Objects;

public class SqlGameDAO implements GameDAO{

    public SqlGameDAO() {
        var statement = """            
                    CREATE TABLE if NOT EXISTS GameTable (
                                    gameID INT NOT NULL,
                                    whiteUsername VARCHAR(255),
                                    blackUsername VARCHAR(255),
                                    gameName VARCHAR(255),
                                    chessGame TEXT,
                                    PRIMARY KEY (gameID)
                                    )""";

        SqlAuthDAO.executeSqlUpdate(statement);
    }

    @Override
    public boolean containsGame(JoinGameData gameData) {
        String statement = String.format("""
                SELECT
                  EXISTS (
                    SELECT 1
                    FROM GameTable
                    WHERE gameID = %s
                  ) AS token_exists""", gameData.gameID()
        );

        return SqlAuthDAO.verifyQuery(statement);
    }

    @Override
    public void deleteAll() {
        String statement = "TRUNCATE UserTable";

        SqlAuthDAO.executeSqlUpdate(statement);
    }

    @Override
    public void createGame(GameData gameData) {
        String statement = String.format("""
                                        INSERT INTO GameTable (
                                        gameID, whiteUsername, blackUsername, gameName, chessGame
                                        ) VALUES(%s, %s, %s, %s, %s)
                                        """,
                gameData.gameID(), gameData.whiteUsername(), gameData.blackUsername(),
                gameData.gameName(), gameData.game().toString()
                );

        SqlAuthDAO.executeSqlUpdate(statement);
    }

    @Override
    public void addPlayer(JoinGameData joinGameData, String userName) {
        String statement = null;
        if (Objects.equals(joinGameData.playerColor(), "WHITE")){
            statement = String.format("""
                            UPDATE GameTable
                            SET whiteUsername=%s
                            WHERE gameID=%s
                            """,
                    userName, joinGameData.gameID()
            );
        }
        else {
            statement = String.format("""
                            UPDATE GameTable
                             SET blackUsername=%s
                             WHERE gameID=%s
                            """,
                    userName, joinGameData.gameID()
            );
        }
    }

    @Override
    public GameData getGame(int gameID) {
        String statement = String.format("SELECT * FROM GameTable WHERE gameID=%s", gameID);

        GameData toReturn = null;
        try { DatabaseManager.createDatabase(); } catch (DataAccessException ex) {
            throw new RuntimeException(ex);
        }
        try (var connection = DatabaseManager.getConnection()) {

            try (var prepareStatement = connection.prepareStatement(statement)) {
                ResultSet rs = prepareStatement.executeQuery();
                if (rs.next()) {
                    toReturn = new GameData(rs.getInt("gameID"),
                            rs.getString("whiteUsername"),
                            rs.getString("blackUsername"),
                            rs.getString("gameName"),
                            rs.getObject("chessGame", ChessGame.class));
                }
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }

        return toReturn;
    }

    @Override
    public Collection<GameData> getAllGames() {
        return null;
    }
}
