package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import handler.GameException;
import handler.UserException;
import model.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameServiceTest {

    private static AuthDAO authDAO;
    private static GameDAO gameDAO;
    private static UserDAO userDAO;
    private static GameService service;
    private static UserService userService;
    private static UserData whitePlayer;
    private static UserData blackPlayer;
    private static String whiteAuth;
    private static String blackAuth;

    @BeforeAll
    public static void init() {

        whitePlayer = new UserData("tlen20", "password", "tlen20@byu.edu");
        blackPlayer = new UserData("kmp1800", "password", "kmp1800@byu.edu");
    }

    @BeforeEach
    public void setUp() {

        authDAO = new AuthDAO();
        gameDAO = new GameDAO();
        userDAO = new UserDAO();
        service = new GameService(gameDAO, authDAO);
        userService = new UserService(authDAO, userDAO);

        try {

            AuthData authDataWhite = userService.register(whitePlayer);
            AuthData authDataBlack = userService.register(blackPlayer);
            whiteAuth = authDataWhite.authToken();
            blackAuth = authDataBlack.authToken();
        }
        catch (UserException e) {

            System.out.println(e.getMessage());
        }
    }

    @Test
    void createGameSuccess() {

        try {
            //
            int gameID = service.createGame(whiteAuth, "NewGame");
            assertEquals(1, gameID);
        }
        catch (GameException e) {

            System.out.println(e.getMessage());
        }
    }

    @Test
    void createGameFailure() {
        //
        String wrongAuth = "This_will_not_work";
        assertThrows(GameException.class, () -> service.createGame(wrongAuth, "name"),
                "Should throw error");

    }

    @Test
    void joinGameSuccess() {
        //
        try {
            //
            JoinGameData joinData = new JoinGameData("WHITE", 1);
            int gameID = service.createGame(whiteAuth, "NewGame");
            assertDoesNotThrow(() -> service.joinGame(whiteAuth, joinData));
        }
        catch (GameException e) {

            System.out.println(e.getMessage());
        }
    }

    @Test
    void joinGameFailure() {
        //
        try {
            JoinGameData joinData = new JoinGameData("WHITE", 2);
            int gameID = service.createGame(whiteAuth, "NewGame");
            assertThrows(GameException.class, () -> service.joinGame(whiteAuth, joinData));
        }
        catch (GameException e) {

            System.out.println(e.getMessage());
        }
    }

    @Test
    void listGamesSuccess() {
        //
        try {
            JoinGameData joinData = new JoinGameData("WHITE", 1);
            JoinGameData joinData2 = new JoinGameData("BLACK", 1);
            int gameID = service.createGame(whiteAuth, "NewGame");
            service.joinGame(whiteAuth, joinData);
            service.joinGame(blackAuth, joinData2);

            // Set up test case -----------------------------------------
            ListGameData testGame = new ListGameData(1, "tlen20",
                    "kmp1800", "NewGame");
            Collection<ListGameData> list = new ArrayList<>();
            list.add(testGame);
            // -----------------------------------------------------------

            assertEquals(list, service.listGames(whiteAuth));

        }
        catch (GameException e) {

            System.out.println(e.getMessage());
        }
    }

    @Test
    void listGamesFailure() {
        //
        try {
            JoinGameData joinData = new JoinGameData("BLACK", 1);
            JoinGameData joinData2 = new JoinGameData("WHITE", 1);
            int gameID = service.createGame(whiteAuth, "NewGame");
            service.joinGame(whiteAuth, joinData);
            service.joinGame(blackAuth, joinData2);

            // Set up test case -----------------------------------------
            ListGameData testGame = new ListGameData(1, "tlen20",
                    "kmp1800", "NewGame");
            Collection<ListGameData> list = new ArrayList<>();
            list.add(testGame);
            // -----------------------------------------------------------
            assertNotEquals(list, service.listGames(whiteAuth));

        }
        catch (GameException e) {

            System.out.println(e.getMessage());
        }
    }
}