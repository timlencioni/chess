package dataaccess;

import handler.UserException;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
import service.UserService;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class SqlUserDAOTest {

    private static UserDAO userDAO;
    private static UserData existingUser;
    private static UserData newUser;

    @BeforeAll
    public static void init() {

        existingUser = new UserData("tlen20", "password", "tlen20@byu.edu");
        newUser = new UserData("kmp1800", "newpassword", "kmp1800@byu.edu");
    }

    @BeforeEach
    public void setUp() {

        userDAO = new SqlUserDAO();
        userDAO.deleteAll();

        userDAO.addUser(existingUser);
    }

    @Test
    void containsUser() {
        assertTrue(userDAO.containsUser(existingUser.username()));
    }

    @Test
    void containsUserFails() {
        assertFalse(userDAO.containsUser(newUser.username()));
    }

    @Test
    void getUser() {
        UserData user = userDAO.getUser(existingUser.username());
        assertEquals(existingUser.username(), user.username());
        assertTrue(BCrypt.checkpw(existingUser.password(), user.password()));
        assertEquals(existingUser.email(), user.email());
    }

    @Test
    void getUserFails() {
        UserData user = userDAO.getUser(existingUser.username());
        assertNotEquals(newUser.username(), user.username());
        assertFalse(BCrypt.checkpw(newUser.password(), user.password()));
        assertNotEquals(newUser.email(), user.email());
    }

    @Test
    void addUser() {
        assertDoesNotThrow(() -> userDAO.addUser(newUser));
        assertEquals(newUser.username(), userDAO.getUser(newUser.username()).username());
    }

    @Test
    void addUserFails() {
        assertThrows(RuntimeException.class, () -> userDAO.addUser(existingUser));
    }

    @Test
    void verifyUser() {
        assertTrue(userDAO.verifyUser(existingUser.username(), existingUser.password()));
    }

    @Test
    void verifyUserFails() {
        assertFalse(userDAO.verifyUser(existingUser.username(), newUser.password()));
    }

    @Test
    void deleteAll() {
        assertDoesNotThrow(() -> userDAO.deleteAll());
    }
}