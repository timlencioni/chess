package server;

import dataaccess.*;
import service.*;
import spark.*;
import handler.*;
import websocket.WebSocketHandler;

public class Server {

    public static AuthDAO authDAO;
    public static GameDAO gameDAO;
    public static UserDAO userDAO;

    public static UserService userService;
    public static UserHandler userHandler;
    public static ClearService clearService;
    public static ClearHandler clearHandler;
    public static GameService gameService;
    public static GameHandler gameHandler;

    public int run(int desiredPort) {
        try { DatabaseManager.createDatabase(); } catch (DataAccessException ex) {
            throw new RuntimeException(ex);
        }

        authDAO = new SqlAuthDAO();
        gameDAO = new SqlGameDAO();
        userDAO = new SqlUserDAO();

        userService = new UserService(authDAO, userDAO);
        userHandler = new UserHandler(userService);
        clearService = new ClearService(authDAO, gameDAO, userDAO);
        clearHandler = new ClearHandler(clearService);
        gameService = new GameService(gameDAO, authDAO);
        gameHandler = new GameHandler(gameService);
        WebSocketHandler wsHandler = new WebSocketHandler();
        wsHandler.setDAO((SqlGameDAO) gameDAO);
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        Spark.webSocket("/ws", wsHandler);

        // Register your endpoints and handle exceptions here.
        Spark.post("/user", userHandler::register);
        Spark.post("/session", userHandler::login);
        Spark.delete("/session", userHandler::logout);

        Spark.delete("/db", clearHandler::clear);

        Spark.post("/game", gameHandler::createGame);
        Spark.put("/game", gameHandler::joinGame);
        Spark.get("/game", gameHandler::listGames);


        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

}
