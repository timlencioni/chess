package service;

import chess.ChessGame;
import dataaccess.*;
import handler.GameException;
import model.AuthData;
import model.GameData;

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
        HashSet<GameData> gameDB = gameDAO.getGamesMemDB();

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
            throw new GameException("Unauthorized");
        }
    }
}
