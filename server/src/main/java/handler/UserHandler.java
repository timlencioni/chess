package handler;

import model.*;
import service.UserService;
import spark.*;
import com.google.gson.Gson;

public class UserHandler {

    private UserService service;

    public UserHandler(UserService service) {
        this.service = service;
    }

    public Object register(Request request, Response response) {

        UserData userData = new Gson().fromJson(request.body(), UserData.class);

        try {

            AuthData authData = service.register(userData);
            response.status(200);
            String res = new Gson().toJson(authData);
            response.body(res);
            return res;
        }
        catch (UserException e) {

            response.status(403);
            return "Error: Username taken"; //toJson or record
        }
    }
}
