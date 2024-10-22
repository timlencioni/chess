package dataaccess;

import model.*;
import java.util.HashMap;

public class AuthDAO {

    private final HashMap<String, AuthData> authMemDB = new HashMap<>();

    public HashMap<String, AuthData> getAuthMemDB() { return authMemDB; }

    public void registerAuth(AuthData authData) {
        authMemDB.put(authData.authToken(), authData);
    }

    public void deleteToken(String authToken) { authMemDB.remove(authToken); }

    public void deleteAll() { authMemDB.clear(); }
}
