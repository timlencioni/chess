package service;

import dataaccess.DataAccess;
import dataaccess.*;
import model.*;

public class UserService {

    private final AuthDAO authDAO;
    private final UserDAO userDAO;

    public UserService(AuthDAO authDAO, UserDAO userDAO) {
        this.authDAO = authDAO;
        this.userDAO = userDAO;
    }

    public AuthData register(UserData user) {
        //TODO
    }
    public AuthData login(UserData user) {
        //TODO
    }
    public void logout(AuthData auth) {
        //TODO
    }

}
