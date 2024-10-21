package dataaccess;

public class DataAccess {

    private final AuthDAO authDAO;
    private final UserDAO userDAO;
    private final GameDAO gameDAO;

    public DataAccess() {
        this.authDAO = new AuthDAO();
        this.userDAO = new UserDAO();
        this.gameDAO = new GameDAO();
    }

    public AuthDAO getAuthDAO() {
        return authDAO;
    }

    public UserDAO getUserDAO() {
        return userDAO;
    }

    public GameDAO getGameDAO() {
        return gameDAO;
    }
}
