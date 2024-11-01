package service;

import chess.ChessGame;
import dataaccess.*;
import handler.GameException;
import model.AuthData;
import model.GameData;
import model.JoinGameData;
import model.ListGameData;

import java.util.Collection;
import java.util.ArrayList;

public class GameService {

    private final GameDAO gameDAO;
    private final AuthDAO authDAO;
    private int newGameID = 0;

    public GameService(GameDAO gameDAO, AuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public int createGame(String authToken, String gameName) throws GameException {

        // HashMap<String, AuthData> authDB = authDAO.getAuthMemDB();
        // HashMap<Integer, GameData> gameDB = gameDAO.getGamesMemDB();

        if (authDAO.containsAuthToken(authToken)) {
            //
            // int gameID = newGameID;
            GameData gameData = new GameData(newGameID,
                    null,null,
                    gameName, new ChessGame());
            gameDAO.createGame(gameData);
            newGameID++;
            return newGameID;
        }
        else {
            throw new GameException("Error: Unauthorized", 401);
        }
    }

    public void joinGame(String authToken, JoinGameData joinGameData) throws GameException {
        //
        // HashMap<String, AuthData> authDB = authDAO.getAuthMemDB();
        // HashMap<Integer, GameData> gameDB = gameDAO.getGamesMemDB();

        if (!gameDAO.containsGame(joinGameData)
                || joinGameData.playerColor() == null) {
            throw new GameException("Error: Bad request", 400);
        }

        if ((joinGameData.playerColor().equals("WHITE") && gameDAO.getGame(joinGameData.gameID()).whiteUsername() != null)
         || (joinGameData.playerColor().equals("BLACK") && gameDAO.getGame(joinGameData.gameID()).blackUsername() != null)) {
            throw new GameException("Error: Side already taken", 403);
        }


        if (authDAO.containsAuthToken(authToken)) {
            //
            String userName = authDAO.getAuth(authToken).username();
            gameDAO.addPlayer(joinGameData, userName);
        }
        else {
            throw new GameException("Error: Unauthorized", 401);
        }
    }

    public Collection<ListGameData> listGames(String authToken) throws GameException {
        //
        // HashMap<String, AuthData> authDB = authDAO.getAuthMemDB();

        if (authDAO.containsAuthToken(authToken)) {
            //
            Collection<GameData> games = gameDAO.getAllGames();
            Collection<ListGameData> listOfGames = new ArrayList<>();
            for (GameData game : games) {

                listOfGames.add(new ListGameData(game.gameID(), game.whiteUsername(),
                                                 game.blackUsername(), game.gameName()));
            }

            return listOfGames;
        }
        else {
            throw new GameException("Error: Unauthorized", 401);
        }
    }
}
