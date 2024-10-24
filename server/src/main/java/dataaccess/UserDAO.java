package dataaccess;

import handler.UserException;
import model.UserData;

import java.util.HashMap;

public class UserDAO {

    private final HashMap<String, UserData> usersMemDB = new HashMap<>();

    public HashMap<String, UserData> getUsersMemDB() {
        return usersMemDB;
    }

    public void addUser(UserData userData) throws UserException {
        usersMemDB.put(userData.username(), userData);
    }

    public void deleteAll() {
        usersMemDB.clear();
    }
}
