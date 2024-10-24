package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ClearServiceTest {

    private static final AuthDAO AUTH_DAO = new AuthDAO();
    private static final UserDAO USER_DAO = new UserDAO();
    private static final GameDAO GAME_DAO = new GameDAO();
    private static final ClearService CLEAR_SERVICE = new ClearService(AUTH_DAO, GAME_DAO, USER_DAO);

    @Test
    void clear() {

        assertDoesNotThrow(CLEAR_SERVICE::clear, "Should not throw error");
    }
}