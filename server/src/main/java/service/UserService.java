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

    public AuthData register(UserData userData) throws UserException, DataAccessException {
        try {

            if (userData.username() == null || userData.password() == null) {
                throw new UserException("Error: Bad Request", 400);
            }

            // Pass to user DataAccessObject
            if (userDAO.containsUser(userData.username())) {
                throw new UserException("Error: Username already taken!", 403);
            }
            else {
                userDAO.addUser(userData);
            }

            return createAuthData(userData);
        }
        catch (UserException e) {
            throw new UserException(e.getMessage(), e.getErrorNum());
        }
        catch (DataAccessException e) {
            throw new DataAccessException(e.getMessage());
        }

    }
    public AuthData login(UserData userData) throws UserException, DataAccessException {
        try {
            AuthData newAuthData;
            if (userDAO.containsUser(userData.username())) {
                if (!userDAO.getUser(userData.username()).password().equals(userData.password())) {

                    throw new UserException("Error: Incorrect password", 401);
                }
                else {
                    newAuthData = createAuthData(userData);
                }

            }
            else {
                throw new UserException("Error: User not registered", 401);
            }

            return newAuthData;

        }
        catch (UserException e) {
            throw new UserException(e.getMessage(), e.getErrorNum());
        }
        catch (DataAccessException e) {
            throw new DataAccessException(e.getMessage());
        }
    }
    public void logout(String authToken) throws UserException {

        if (authDAO.containsAuthToken(authToken)) {
            authDAO.deleteAuth(authToken);
        }
        else {
            throw new UserException("Error: Invalid AuthToken", 401);
        }

    }

    public AuthData createAuthData(UserData userData) throws DataAccessException {
        // Create new auth data
        String newToken = UUID.randomUUID().toString();
        AuthData newAuthData = new AuthData(userData.username(), newToken);

        // Pass to auth DataAccessObject
        authDAO.addAuth(newAuthData);
        return newAuthData;
    }

}
