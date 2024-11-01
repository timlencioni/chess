package service;

import dataaccess.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ClearServiceTest {

    private static final MemAuthDAO AUTH_DAO = new MemAuthDAO();
    private static final MemUserDAO USER_DAO = new MemUserDAO();
    private static final GameDAO GAME_DAO = new MemGameDAO();
    private static final ClearService CLEAR_SERVICE = new ClearService(AUTH_DAO, GAME_DAO, USER_DAO);

    @Test
    void clear() {

        assertDoesNotThrow(CLEAR_SERVICE::clear, "Should not throw error");
    }
}