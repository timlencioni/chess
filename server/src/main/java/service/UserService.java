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

            return createAuthData(userData);
        }
        catch (UserException e) {
            // Throw exception from earlier
            throw new UserException(e.getMessage());
        }

    }
    public AuthData login(UserData userData) throws UserException {
        try {
            // Pass to user DataAccessObject
            userDAO.login(userData);

            return createAuthData(userData);

        }
        catch (UserException e) {
            // Throw exception from earlier
            throw new UserException(e.getMessage());
        }
    }
    public void logout(String authToken) throws UserException {

        if (authDAO.getAuthMemDB().containsKey(authToken)) {

            authDAO.deleteToken(authToken);
        }
        else {

            throw new UserException("Invalid AuthToken");
        }

    }

    public AuthData createAuthData(UserData userData) {

        // Create new auth data
        String newToken = UUID.randomUUID().toString();
        AuthData newAuthData = new AuthData(userData.username(), newToken);

        // Pass to auth DataAccessObject
        authDAO.registerAuth(newAuthData);
        return newAuthData;
    }

}
