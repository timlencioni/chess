package client;

import chess.ChessGame;
import exception.ResponseException;
import model.GameData;
import model.JoinGameData;
import model.UserData;
import org.junit.jupiter.api.*;
import server.Server;
import server.ServerFacade;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(8080);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade(port);
    }

    @BeforeEach
    public void setUp() {
        server.stop();
        var port = server.run(8080);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade(port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @Test
    void register() throws Exception {
        var authData = facade.register(new UserData("player1", "password", "p1@email.com"));
        Assertions.assertTrue(authData.authToken().length() > 10);
    }

    @Test
    void registerFails() {
        Assertions.assertThrows(ResponseException.class,
                () -> facade.register(new UserData("player1","password", null)));
    }

    @Test
    void login() throws Exception {
        UserData user = new UserData("player1", "password", "p1@email.com");
        var authData = facade.register(user);
        facade.logout(authData.authToken());
        authData = facade.login(new UserData("player1", "password", null));
        Assertions.assertTrue(authData.authToken().length() > 10);
    }

    @Test
    void loginFails() throws Exception {
        UserData user = new UserData("player1", "password", "p1@email.com");
        var authData = facade.register(user);
        facade.logout(authData.authToken());
        Assertions.assertThrows(ResponseException.class,
                () ->facade.login(new UserData("player1", "wrong", null)));

    }

    @Test
    void logout() throws Exception {
        UserData user = new UserData("player1", "password", "p1@email.com");
        var authData = facade.register(user);
        Assertions.assertDoesNotThrow(() -> facade.logout(authData.authToken()));
    }

    @Test
    void logoutFails() throws Exception {
        UserData user = new UserData("player1", "password", "p1@email.com");
        var authData = facade.register(user);
        Assertions.assertThrows(ResponseException.class, () -> facade.logout(null));
    }

    @Test
    void clear() {
        Assertions.assertDoesNotThrow(() -> facade.clear());
    }

    @Test
    void createGame() throws Exception {
        UserData user = new UserData("player1", "password", "p1@email.com");
        var authData = facade.register(user);
        GameData gameData = new GameData(0, null, null,
                "game", new ChessGame());
        GameData newGameData = facade.createGame(gameData, authData.authToken());
        Assertions.assertEquals(1, newGameData.gameID());
    }

    @Test
    void createGameFails() throws Exception {
        GameData gameData = new GameData(0, null, null,
                "game", new ChessGame());
        Assertions.assertThrows(ResponseException.class,
                () -> facade.createGame(gameData, "Unauthorized_authToken"));
    }

    @Test
    void joinGame() throws Exception{
        UserData user = new UserData("player1", "password", "p1@email.com");
        var authData = facade.register(user);
        GameData gameData = new GameData(0, null, null,
                "game", new ChessGame());
        GameData newGameData = facade.createGame(gameData, authData.authToken());

        JoinGameData join = new JoinGameData("WHITE", 1);
        Assertions.assertDoesNotThrow(() -> facade.joinGame(join, authData.authToken()));
    }

    @Test
    void joinGameFails() throws Exception {
        UserData user = new UserData("player1", "password", "p1@email.com");
        var authData = facade.register(user);
        GameData gameData = new GameData(0, null, null,
                "game", new ChessGame());
        GameData newGameData = facade.createGame(gameData, authData.authToken());

        JoinGameData join = new JoinGameData("WHITE", 1);
        facade.joinGame(join, authData.authToken());

        Assertions.assertThrows(ResponseException.class,
                () -> facade.joinGame(join, authData.authToken()));
    }

    @Test
    void listGames() throws Exception {
        UserData user = new UserData("player1", "password", "p1@email.com");
        var authData = facade.register(user);
        GameData gameData = new GameData(0, null, null,
                "game", new ChessGame());
        GameData newGameData = facade.createGame(gameData, authData.authToken());

        Assertions.assertEquals(1, facade.listGames(authData.authToken()).games().size());
    }

    @Test
    void listGamesFails() throws Exception {
        UserData user = new UserData("player1", "password", "p1@email.com");
        var authData = facade.register(user);
        GameData gameData = new GameData(0, null, null,
                "game", new ChessGame());
        GameData newGameData = facade.createGame(gameData, authData.authToken());

        Assertions.assertThrows(ResponseException.class,
                () -> facade.listGames("Unauthorized Token"));
    }

}
