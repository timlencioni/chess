package dataaccess;

import handler.UserException;
import model.UserData;

import java.util.HashMap;

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
}
