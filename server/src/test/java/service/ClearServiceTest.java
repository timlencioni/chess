package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import model.UserData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ClearServiceTest {

    private static final AuthDAO authDAO = new AuthDAO();
    private static final UserDAO userDAO = new UserDAO();
    private static final GameDAO gameDAO = new GameDAO();
    private static final ClearService service = new ClearService(authDAO, gameDAO, userDAO);

    @Test
    void clear() {

        assertDoesNotThrow(service::clear, "Should not throw error");
    }
}