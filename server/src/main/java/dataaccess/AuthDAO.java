package dataaccess;

import model.AuthData;

import java.util.HashMap;

public interface AuthDAO {

    boolean containsAuthToken(String authToken);

    void addAuth(AuthData authData);

    void deleteAuth(String authToken);

    void deleteAll();

    AuthData getAuth(String authToken);

}
