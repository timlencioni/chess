package dataaccess;

import model.*;

import java.util.HashMap;

public class MemAuthDAO implements AuthDAO{

    private final HashMap<String, AuthData> authMemDB = new HashMap<>();

    @Override
    public boolean containsAuthToken(String authToken) {
        return authMemDB.containsKey(authToken);
    }

    @Override
    public void addAuth(AuthData authData) {
        authMemDB.put(authData.authToken(), authData);
    }

    @Override
    public void deleteAuth(String authToken) {
        authMemDB.remove(authToken);
    }

    @Override
    public void deleteAll() {
        authMemDB.clear();
    }

    @Override
    public AuthData getAuth(String authToken) {
        return authMemDB.get(authToken);
    }
}
