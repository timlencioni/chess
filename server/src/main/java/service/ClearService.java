package service;

import dataaccess.*;
import handler.ClearException;

public class ClearService {

    private final AuthDAO authDAO;
    private final GameDAO gameDAO;
    private final UserDAO userDAO;

    public ClearService(AuthDAO authDAO, GameDAO gameDAO, UserDAO userDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
        this.userDAO = userDAO;
    }

    public void clear() throws ClearException {
        try {
            authDAO.deleteAll();
        }
        catch (Exception e){
            throw new ClearException("AuthData did not clear");
        }

        try {
            gameDAO.deleteAll();
        }
        catch (Exception e){
            throw new ClearException("GameData did not clear");
        }

        try {
            userDAO.deleteAll();
        }
        catch (Exception e){
            throw new ClearException("UserData did not clear");
        }
    }
}
