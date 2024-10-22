package dataaccess;

import model.GameData;

import java.util.HashSet;

public class GameDAO {

    private final HashSet<GameData> gamesMemDB = new HashSet<>();

    public void deleteAll() { gamesMemDB.clear(); }
}
