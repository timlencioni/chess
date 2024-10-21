package dataaccess;

import model.AuthData;

import java.util.HashMap;
import java.util.HashSet;

public class AuthDAO {

    private final HashSet<AuthData> authMemDB = new HashSet<>();

    public void registerAuth(AuthData authData) {
        authMemDB.add(authData);
    }
}
