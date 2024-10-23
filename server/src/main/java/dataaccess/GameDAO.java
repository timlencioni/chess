package dataaccess;

import model.GameData;

import java.util.HashSet;

public class GameDAO {

    private final HashSet<GameData> gamesMemDB = new HashSet<>();

    public HashSet<GameData> getGamesMemDB() { return gamesMemDB; }

    public void deleteAll() { gamesMemDB.clear(); }

    public void createGame(GameData gameData) { gamesMemDB.add(gameData); }
}
