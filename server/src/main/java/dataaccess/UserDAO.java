package dataaccess;

import handler.UserException;
import model.UserData;

public interface UserDAO {

    boolean containsUser(String username);

    UserData getUser(String username);

    void addUser(UserData userData);

    void deleteAll();
}
