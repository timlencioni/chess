package dataaccess;

import chess.ChessGame;
import model.GameData;
import model.JoinGameData;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import com.google.gson.Gson;

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
    public boolean containsGame(JoinGameData gameData) {
        var toReturn = new ArrayList<>();
        try { DatabaseManager.createDatabase(); } catch (DataAccessException ex) {
            throw new RuntimeException(ex);
        }
        try (var connection = DatabaseManager.getConnection()) {
            String statement = "SELECT gameID FROM GameTable";
            try (var prepareStatement = connection.prepareStatement(statement)) {
                ResultSet rs = prepareStatement.executeQuery();
                while (rs.next()) {
                    toReturn.add(rs.getInt("gameID"));
                }
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }

        return toReturn.contains(gameData.gameID());
    }

    @Override
    public void deleteAll() {
        try { DatabaseManager.createDatabase(); } catch (DataAccessException ex) {
            throw new RuntimeException(ex);
        }
        try (var connection = DatabaseManager.getConnection()) {
            String statement = "TRUNCATE GameTable";
            try (var prepareStatement = connection.prepareStatement(statement)) {
                prepareStatement.executeUpdate();
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void createGame(GameData gameData) {
        String statement = """
                            INSERT INTO GameTable (
                            gameID, whiteUsername, blackUsername, gameName, chessGame
                            ) VALUES(?, ?, ?, ?, ?)
                            """;

        try { DatabaseManager.createDatabase(); } catch (DataAccessException ex) {
            throw new RuntimeException(ex);
        }
        try (var connection = DatabaseManager.getConnection()) {

            try (var prepareStatement = connection.prepareStatement(statement)) {
                prepareStatement.setInt(1, gameData.gameID());
                prepareStatement.setString(2, gameData.whiteUsername());
                prepareStatement.setString(3, gameData.blackUsername());
                prepareStatement.setString(4, gameData.gameName());
                prepareStatement.setString(5, serializeChessGame(gameData.game()));
                prepareStatement.executeUpdate();

            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private String serializeChessGame(ChessGame game) {
        return new Gson().toJson(game);
    }

    private ChessGame deserializeChessGame(String serialChessGame) {
        return new Gson().fromJson(serialChessGame, ChessGame.class);
    }

    @Override
    public void addPlayer(JoinGameData joinGameData, String userName) {
        try { DatabaseManager.createDatabase(); } catch (DataAccessException ex) {
            throw new RuntimeException(ex);
        }
        String statement = null;
        if (Objects.equals(joinGameData.playerColor(), "WHITE")){
            try (var connection = DatabaseManager.getConnection()) {
                statement = """
                        UPDATE GameTable
                        SET whiteUsername=?
                        WHERE gameID=?
                        """;
                try (var prepareStatement = connection.prepareStatement(statement)) {
                    prepareStatement.setString(1, userName);
                    prepareStatement.setInt(2, joinGameData.gameID());
                    prepareStatement.executeUpdate();
                }
            } catch (SQLException | DataAccessException e) {
                throw new RuntimeException(e);
            }

        }
        else {


            try (var connection = DatabaseManager.getConnection()) {
                statement = """
                        UPDATE GameTable
                        SET blackUsername=?
                        WHERE gameID=?
                        """;
                try (var prepareStatement = connection.prepareStatement(statement)) {
                    prepareStatement.setString(1, userName);
                    prepareStatement.setInt(2, joinGameData.gameID());
                    prepareStatement.executeUpdate();
                }
            } catch (SQLException | DataAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public GameData getGame(int gameID) {
        String statement = "SELECT * FROM GameTable WHERE gameID=?";

        GameData toReturn = null;
        try { DatabaseManager.createDatabase(); } catch (DataAccessException ex) {
            throw new RuntimeException(ex);
        }
        try (var connection = DatabaseManager.getConnection()) {

            try (var prepareStatement = connection.prepareStatement(statement)) {
                prepareStatement.setInt(1, gameID);
                ResultSet rs = prepareStatement.executeQuery();
                if (rs.next()) {
                    toReturn = new GameData(rs.getInt("gameID"),
                            rs.getString("whiteUsername"),
                            rs.getString("blackUsername"),
                            rs.getString("gameName"),
                            deserializeChessGame(rs.getString("chessGame")));
                }
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }

        return toReturn;
    }

    @Override
    public Collection<GameData> getAllGames() {

        var toReturn = new ArrayList<GameData>();
        try { DatabaseManager.createDatabase(); } catch (DataAccessException ex) {
            throw new RuntimeException(ex);
        }
        try (var connection = DatabaseManager.getConnection()) {
            String statement = "SELECT * FROM GameTable";
            try (var prepareStatement = connection.prepareStatement(statement)) {
                ResultSet rs = prepareStatement.executeQuery();
                while (rs.next()) {
                    int gameID = rs.getInt("gameID");
                    String whiteUser = rs.getString("whiteUsername");
                    String blackUser = rs.getString("blackUsername");
                    String gameName = rs.getString("gameName");
                    String game = rs.getString("chessGame");
                    toReturn.add(new GameData(gameID, whiteUser, blackUser, gameName, deserializeChessGame(game)));
                }
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }

        return toReturn;
    }

}
