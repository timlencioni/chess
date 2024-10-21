package handler;

import model.*;
import service.UserService;
import spark.*;
import com.google.gson.Gson;

public class UserHandler {

    private static UserService service;

    public UserHandler(UserService service) {
        UserHandler.service = service;
    }

    public static Object register(Request request, Response response) throws UserException {

        UserData userData = new Gson().fromJson(request.body(), UserData.class);

        try {

            AuthData authData = service.register(userData);
            response.status(200);
            return new Gson().toJson(authData);
        }
        catch (UserException e) {

            response.status(403);
            return "Error: Username taken";
        }
    }
}
