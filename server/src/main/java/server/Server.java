package server;

import dataaccess.*;
import service.*;
import spark.*;
import handler.*;

public class Server {

    public int run(int desiredPort) {
        try { DatabaseManager.createDatabase(); } catch (DataAccessException ex) {
            throw new RuntimeException(ex);
        }

        AuthDAO authDAO = new SqlAuthDAO();
        GameDAO gameDAO = new SqlGameDAO();
        UserDAO userDAO = new SqlUserDAO();

        UserService userService = new UserService(authDAO, userDAO);
        UserHandler userHandler = new UserHandler(userService);
        ClearService clearService = new ClearService(authDAO, gameDAO, userDAO);
        ClearHandler clearHandler = new ClearHandler(clearService);
        GameService gameService = new GameService(gameDAO, authDAO);
        GameHandler gameHandler = new GameHandler(gameService);

        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

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
