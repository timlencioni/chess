package dataaccess;

import model.GameData;
import model.JoinGameData;

import java.util.Collection;
import java.util.HashMap;

public class MemGameDAO implements GameDAO{

    private final HashMap<Integer, GameData> gamesMemDB = new HashMap<>();

    public HashMap<Integer, GameData> getGamesMemDB() {
        return gamesMemDB;
    }

    @Override
    public boolean containsGame(JoinGameData gameData) {
        return gamesMemDB.containsKey(gameData.gameID());
    }

    @Override
    public void deleteAll() {
        gamesMemDB.clear();
    }

    @Override
    public void createGame(GameData gameData) {
        gamesMemDB.put(gameData.gameID(), gameData);
    }

    @Override
    public void addPlayer(JoinGameData joinGameData, String userName) {

        if (gamesMemDB.containsKey(joinGameData.gameID())) {

            GameData currGame = gamesMemDB.get(joinGameData.gameID());
            GameData newGameData;
            if (joinGameData.playerColor().equals("WHITE")) {
                newGameData = new GameData(joinGameData.gameID(), userName, currGame.blackUsername(),
                        currGame.gameName(), currGame.game());
            } else {
                newGameData = new GameData(joinGameData.gameID(), currGame.whiteUsername(), userName,
                        currGame.gameName(), currGame.game());
            }
            gamesMemDB.remove(currGame.gameID());
            gamesMemDB.put(newGameData.gameID(), newGameData);
        }

    }

    @Override
    public GameData getGame(int gameID) {
        return gamesMemDB.get(gameID);
    }

    @Override
    public Collection<GameData> getAllGames() {
        return gamesMemDB.values();
    }
}
