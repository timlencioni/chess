package service;

import chess.ChessGame;
import dataaccess.*;
import handler.GameException;
import model.AuthData;
import model.GameData;
import model.JoinGameData;

import java.util.HashMap;
import java.util.HashSet;

public class GameService {

    GameDAO gameDAO;
    AuthDAO authDAO;

    public GameService(GameDAO gameDAO, AuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public int createGame(String authToken, String gameName) throws GameException {

        HashMap<String, AuthData> authDB = authDAO.getAuthMemDB();
        HashMap<Integer, GameData> gameDB = gameDAO.getGamesMemDB();

        if (authDB.containsKey(authToken)) {
            //
            int gameID = gameDB.size() + 1;
            GameData gameData = new GameData(gameID,
                    null,null,
                    gameName, new ChessGame());
            gameDAO.createGame(gameData);

            return gameID;
        }
        else {
            throw new GameException("Unauthorized", 401);
        }
    }

    public void joinGame(String authToken, JoinGameData joinGameData) throws GameException {

        HashMap<String, AuthData> authDB = authDAO.getAuthMemDB();
        HashMap<Integer, GameData> gameDB = gameDAO.getGamesMemDB();
        if (!gameDB.containsKey(joinGameData.gameID())) {
            throw new GameException("Bad request", 400);
        }

        if ((joinGameData.playerColor().equals("WHITE") && gameDB.get(joinGameData.gameID()).whiteUsername() != null)
         || (joinGameData.playerColor().equals("BLACK") && gameDB.get(joinGameData.gameID()).blackUsername() != null)) {
            throw new GameException("Side already taken", 403);
        }


        if (authDB.containsKey(authToken)) {
            //
            String userName = authDB.get(authToken).userName();
            gameDAO.addPlayer(joinGameData, userName);
        }
        else {
            throw new GameException("Unauthorized", 401);
        }
    }
}
