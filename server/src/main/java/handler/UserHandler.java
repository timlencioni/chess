package handler;

import dataaccess.DataAccessException;
import model.*;
import service.UserService;
import spark.*;
import com.google.gson.Gson;
import java.util.HashMap;

public class UserHandler {

    private final UserService service;

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
            return handleException(response, e);
        }
        catch (DataAccessException e) {
            return handleDataException(response, e);
        }
    }

    private Object handleDataException(Response response, DataAccessException e) {
        response.status(500);
        Gson gson = new Gson();
        HashMap<String, String> excMap = new HashMap<String, String>();
        excMap.put("message", e.getMessage());
        return gson.toJson(excMap);
    }

    private String handleException(Response response, UserException e) {
        response.status(e.getErrorNum());
        Gson gson = new Gson();
        HashMap<String, String> excMap = new HashMap<String, String>();
        excMap.put("message", e.getMessage());
        return gson.toJson(excMap);
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

            return handleException(response, e);
        }
        catch (DataAccessException e) {

            return handleDataException(response, e);
        }

    }

    public Object logout(Request request, Response response) throws UserException {

        String authToken = request.headers("authorization");

        try {
            service.logout(authToken);
            response.status(200);
            String res = "{}";
            response.body(res);
            return res;
        }
        catch (UserException e) {
            return handleException(response, e);
        }
    }


}
