package dataaccess;

import model.UserData;

public interface UserDAO {

    boolean containsUser(String username);

    UserData getUser(String username);

    void addUser(UserData userData);

    boolean verifyUser(String username, String password);

    void deleteAll();
}
