package server;

import dataaccess.*;
import service.*;
import spark.*;
import handler.*;

public class Server {

    public int run(int desiredPort) {

        AuthDAO authDAO = new AuthDAO();
        UserDAO userDAO = new UserDAO();
        UserService service = new UserService(authDAO, userDAO);
        UserHandler userHandler = new UserHandler(service);

        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.post("/user", userHandler::register);

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
