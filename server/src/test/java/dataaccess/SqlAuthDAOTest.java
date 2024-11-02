package dataaccess;

import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class SqlAuthDAOTest {

    private AuthDAO authDAO;
    private static AuthData authData;

    @BeforeAll
    public static void init() {
        authData = new AuthData("tlen20", "newToken");
    }

    @BeforeEach
    public void setUp() {

        authDAO = new SqlAuthDAO();
        authDAO.deleteAll();

        authDAO.addAuth(authData);
    }

    @Test
    void containsAuthToken() {

        assertTrue(authDAO.containsAuthToken(authData.authToken()));
    }

    @Test
    void containsAuthTokenFails() {

        assertFalse(authDAO.containsAuthToken(authData.username()));
    }

    @Test
    void addAuth() {
        AuthData newAuthData = new AuthData("kmp1800", "newToken2");

        authDAO.addAuth(newAuthData);

        assertTrue(authDAO.containsAuthToken(newAuthData.authToken()));
    }

    @Test
    void addAuthFails() {
        AuthData newAuthData = new AuthData("kmp1800", "newToken");

        assertThrows(RuntimeException.class, () -> authDAO.addAuth(newAuthData));
    }

    @Test
    void deleteAuth() {
        authDAO.deleteAuth(authData.authToken());
        assertFalse(authDAO.containsAuthToken(authData.authToken()));
    }

    @Test
    void deleteAuthFails() {
        authDAO.deleteAuth(authData.username());
        assertTrue(authDAO.containsAuthToken(authData.authToken()));
    }

    @Test
    void deleteAll() {
        assertDoesNotThrow(() -> authDAO.deleteAll());
    }

    @Test
    void getAuth() {
        assertEquals(authData, authDAO.getAuth(authData.authToken()));
    }

    @Test
    void getAuthFails() {
        AuthData newAuthData = new AuthData("kmp1800", "newToken");

        assertNotEquals(newAuthData, authDAO.getAuth(newAuthData.authToken()));
    }
}