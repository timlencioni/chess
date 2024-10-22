package service;

import dataaccess.*;
import handler.UserException;
import model.*;

import java.util.UUID;

public class UserService {

    private final AuthDAO authDAO;
    private final UserDAO userDAO;

    public UserService(AuthDAO authDAO, UserDAO userDAO) {
        this.authDAO = authDAO;
        this.userDAO = userDAO;
    }

    public AuthData register(UserData userData) throws UserException {
        try {
            // Pass to user DataAccessObject
            userDAO.register(userData);

            // Create new auth data
            String newToken = UUID.randomUUID().toString();
            AuthData newAuthData = new AuthData(userData.username(), newToken);

            // Pass to auth DataAccessObject
            authDAO.registerAuth(newAuthData);
            return newAuthData;
        }
        catch (UserException e) {
            // Throw exception from earlier
            throw new UserException(e.getMessage());
        }

    }
    public AuthData login(UserData userData) {
        //TODO
        return new AuthData("user", "123");
    }
    public void logout(AuthData authData) {
        //TODO
    }

}
