package handler;

import model.*;
import service.UserService;
import spark.*;
import com.google.gson.Gson;
import java.util.HashMap;

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
            Gson gson = new Gson();
            HashMap<String, String> exc_map = new HashMap<String, String>();
            exc_map.put("message", e.toString());
            return gson.toJson(exc_map);
            // return "{ \"message\": \"Error: Username already taken\" }"; //TODO: toJson or record
        }
    }
}
