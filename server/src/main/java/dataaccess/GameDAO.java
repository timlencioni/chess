package dataaccess;

import model.GameData;
import model.JoinGameData;

import java.util.Collection;

public interface GameDAO {

    boolean containsGame(JoinGameData gameData);

    void deleteAll();

    void createGame(GameData gameData);

    void addPlayer(JoinGameData joinGameData, String userName);

    GameData getGame(int gameID);

    Collection<GameData> getAllGames();

}
