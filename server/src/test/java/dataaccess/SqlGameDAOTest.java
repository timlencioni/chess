package dataaccess;

import chess.ChessGame;
import handler.GameException;
import model.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.GameService;
import service.UserService;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class SqlGameDAOTest {

    private GameDAO gameDAO;
    private AuthDAO authDAO;
    private UserDAO userDAO;
    private static UserData whitePlayer;
    private static UserData blackPlayer;
    private static GameData existingGame;
    private static GameData newGame;

    private static AuthData whiteAuth;

    private static GameService service;
    private static UserService userService;

    @BeforeAll
    public static void init() {
        existingGame = new GameData(0, null, null,
                "Test Game", new ChessGame());
        newGame = new GameData(1, "tlen20", null,
                "Test Game 2", new ChessGame());
        whitePlayer = new UserData("tlen20", "password", "tlen20@byu.edu");
        blackPlayer = new UserData("kmp1800", "password", "kmp1800@byu.edu");
    }

    @BeforeEach
    public void setUp() throws Exception {
        gameDAO = new SqlGameDAO();
        gameDAO.deleteAll();

        authDAO = new MemAuthDAO();
        userDAO = new MemUserDAO();

        userService = new UserService(authDAO, userDAO);
        try {
            whiteAuth = userService.createAuthData(whitePlayer);
        }
        catch (Exception e) {
            throw new Exception(e.getMessage());
        }

        gameDAO.createGame(existingGame);
        authDAO.addAuth(whiteAuth);

        service = new GameService(gameDAO, authDAO);
    }

    @Test
    void containsGame() {
        assertTrue(gameDAO.containsGame(new JoinGameData("WHITE", 0)));
    }

    @Test
    void containsGameFails() {
        assertFalse(gameDAO.containsGame(new JoinGameData("WHITE", 100)));
    }

    @Test
    void deleteAll() {
        assertDoesNotThrow(() -> gameDAO.deleteAll());
    }

    @Test
    void createGame() {
        gameDAO.createGame(new GameData(1, null, null,
                "Test Game 2", new ChessGame()));
        assertTrue(gameDAO.containsGame(new JoinGameData("WHITE", 1)));
    }

    @Test
    void createGameFails() {
        GameData newGame = new GameData(1, null, null,null, new ChessGame());
        assertThrows(RuntimeException.class, () -> gameDAO.createGame(newGame));
    }

    @Test
    void addPlayer() {
        assertDoesNotThrow(() ->
                gameDAO.addPlayer(new JoinGameData("WHITE", 0), whitePlayer.username()));
    }

    @Test
    void addPlayerFails() {
        try {
            JoinGameData joinData = new JoinGameData("WHITE", 2);
            int gameID = service.createGame(whiteAuth.authToken(), "NewGame");
            assertThrows(GameException.class, () -> service.joinGame(whiteAuth.authToken(), joinData));
        }
        catch (GameException e) {

            System.out.println(e.getMessage());
        }
    }

    @Test
    void getGame() {
        assertEquals(gameDAO.getGame(0).gameName(), existingGame.gameName());
    }

    @Test
    void getGameFails() {
        gameDAO.createGame(newGame);
        assertNotEquals(gameDAO.getGame(1).gameName(), existingGame.gameName());
    }

    @Test
    void getAllGames() {
        assertDoesNotThrow(() -> gameDAO.getAllGames());
    }

    @Test
    void getAllGamesFails() {
        ArrayList<GameData> notEqual = new ArrayList<>();
        notEqual.add(newGame);

        assertNotEquals(notEqual, gameDAO.getAllGames());
    }
}