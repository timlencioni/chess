package dataaccess;

import model.GameData;
import model.JoinGameData;

import java.util.Collection;
import java.util.HashMap;

public class GameDAO {

    private final HashMap<Integer, GameData> gamesMemDB = new HashMap<>();

    public HashMap<Integer, GameData> getGamesMemDB() {
        return gamesMemDB;
    }

    public void deleteAll() {
        gamesMemDB.clear();
    }

    public void createGame(GameData gameData) {
        gamesMemDB.put(gameData.gameID(), gameData);
    }

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

    public Collection<GameData> getAllGames() {
        return gamesMemDB.values();
    }
}
