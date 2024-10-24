package service;

import handler.UserException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import model.*;
import dataaccess.*;

class UserServiceTest {

    private static AuthDAO authDAO;
    private static UserDAO userDAO;
    private static UserService service;
    private static UserData existingUser;
    private static UserData newUser;
    private static String existingAuth;

    @BeforeAll
    public static void init() {

        existingUser = new UserData("tlen20", "password", "tlen20@byu.edu");
        newUser = new UserData("kmp1800", "password", "kmp1800@byu.edu");
    }

    @BeforeEach
    public void setUp() {

        authDAO = new AuthDAO();
        userDAO = new UserDAO();
        service = new UserService(authDAO, userDAO);

        try {
            AuthData authData = service.register(existingUser);
            existingAuth = authData.authToken();
        }
        catch (UserException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    void registerSuccess() {

        try {
            AuthData authData = service.register(newUser);
            assertNotNull(authData);
            assertEquals("kmp1800", authData.username());
        }
        catch (UserException e) {
            assertNull(e, "Error thrown not expected");
        }

    }

    @Test
    void registerFailure() {

        UserData userData = new UserData("username", null, "email");
        assertThrows(UserException.class, () -> service.register(userData), "Should throw Error");
    }

    @Test
    void loginSuccess() {

        try {
            AuthData authData = service.login(existingUser);
            assertNotNull(authData);
            assertEquals("tlen20", authData.username());
        }
        catch (UserException e) {
            assertNull(e, "Error thrown not expected");
        }
    }

    @Test
    void loginFailure() {

        UserData userData = new UserData("kmp1800", "passwordd", "email");
        assertThrows(UserException.class, () -> service.login(userData), "Should throw Error");
    }

    @Test
    void logoutSuccess() {

        assertDoesNotThrow(() -> service.logout(existingAuth), "Should throw Error");
    }

    @Test
    void logoutFailure() {

        String wrongAuthToken = "This_is_wrong";
        assertThrows(UserException.class, () -> service.logout(wrongAuthToken), "Should throw Error");
    }
}