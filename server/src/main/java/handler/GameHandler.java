package handler;

import com.google.gson.Gson;
import service.GameService;
import spark.Request;
import spark.Response;

import java.util.HashMap;

public class GameHandler {

    private GameService service;

    public GameHandler(GameService service) {

        this.service = service;
    }

    public Object createGame(Request request, Response response) {

        String authToken = request.headers("authorization");
        String gameName = new Gson().fromJson(request.body(), String.class);

        try {

            int gameID = service.createGame(authToken, gameName);
            response.status(200);
            String res = new Gson().toJson(gameID);
            response.body(res);
            return res;
        }
        catch (GameException e) {
            response.status(400);
            Gson gson = new Gson();
            HashMap<String, String> exc_map = new HashMap<String, String>();
            exc_map.put("message", e.toString());
            return gson.toJson(exc_map);
        }
    }
}
