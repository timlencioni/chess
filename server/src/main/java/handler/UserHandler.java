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

            response.status(e.getErrorNum());
            Gson gson = new Gson();
            HashMap<String, String> exc_map = new HashMap<String, String>();
            exc_map.put("message", e.getMessage());
            return gson.toJson(exc_map);
        }
    }

    public Object login(Request request, Response response) {

        UserData userData = new Gson().fromJson(request.body(), UserData.class);

        try {

            AuthData authData = service.login(userData);
            response.status(200);
            String res = new Gson().toJson(authData);
            response.body(res);
            return res;
        }
        catch (UserException e) {

            response.status(e.getErrorNum());
            Gson gson = new Gson();
            HashMap<String, String> exc_map = new HashMap<String, String>();
            exc_map.put("message", e.getMessage());
            return gson.toJson(exc_map);
        }

    }

    public Object logout(Request request, Response response) throws UserException {

        String authToken = request.headers("authorization");

        try {
            service.logout(authToken);
            response.status(200);
            String res = new Gson().toJson("");
            response.body(res);
            return res;
        }
        catch (UserException e) {
            response.status(e.getErrorNum());
            Gson gson = new Gson();
            HashMap<String, String> exc_map = new HashMap<String, String>();
            exc_map.put("message", e.getMessage());
            return gson.toJson(exc_map);
        }
    }


}
