package dataaccess;

import handler.UserException;
import model.UserData;

import java.util.HashMap;

public class MemUserDAO implements UserDAO{

    private final HashMap<String, UserData> usersMemDB = new HashMap<>();

    @Override
    public boolean containsUser(String username) {
        return usersMemDB.containsKey(username);
    }

    @Override
    public UserData getUser(String username) {
        return usersMemDB.get(username);
    }

    public void addUser(UserData userData) {
        usersMemDB.put(userData.username(), userData);
    }

    @Override
    public boolean verifyUser(String username, String password) {
        String passwordOnFile = usersMemDB.get(username).password();
        return password.equals(passwordOnFile);
    }

    public void deleteAll() {
        usersMemDB.clear();
    }
}
