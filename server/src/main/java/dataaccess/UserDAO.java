package dataaccess;

import handler.UserException;
import model.AuthData;
import model.UserData;

import java.util.HashMap;

// FIXME:: Move logic to UserService

public class UserDAO {

    private final HashMap<String, UserData> usersMemDB = new HashMap<>();

    public void register(UserData userData) throws UserException {

        if (usersMemDB.containsKey(userData.username())) {

            throw new UserException("Username already taken!");
        }
        else {
            usersMemDB.put(userData.username(), userData);
        }
    }

    public void login(UserData userData) throws UserException {

        if (usersMemDB.containsKey(userData.username())) {
            if (!usersMemDB.get(userData.username()).password().equals(userData.password())) {

                throw new UserException("Incorrect password");
            }

        }
        else {
            throw new UserException("User not registered");
        }
    }

    public void deleteAll() { usersMemDB.clear(); }
}
