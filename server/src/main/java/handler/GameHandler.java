package handler;

import com.google.gson.Gson;
import model.*;
import service.GameService;
import spark.Request;
import spark.Response;

import java.util.Collection;
import java.util.HashMap;

public class GameHandler {

    private final GameService service;

    public GameHandler(GameService service) {
        this.service = service;
    }

    private String handleGameException(Response response, GameException gameException) {
        response.status(gameException.getErrorNum());
        Gson gson = new Gson();
        HashMap<String, String> excMap = new HashMap<String, String>();
        excMap.put("message", gameException.getMessage());
        return gson.toJson(excMap);
    }

    public Object createGame(Request request, Response response) {

        String authToken = request.headers("authorization");
        //String gameName = request.body();
        GameData tempGameData = new Gson().fromJson(request.body(), GameData.class);
        String gameName = tempGameData.gameName();

        try {
            int gameID = service.createGame(authToken, gameName);
            CreateGameData createGameData = new CreateGameData(gameID);
            response.status(200);
            String res = new Gson().toJson(createGameData);
            response.body(res);
            return res;
        } catch (GameException e) {
            return handleGameException(response, e);
        }
    }

    public Object joinGame(Request request, Response response) {

        String authToken = request.headers("authorization");

        JoinGameData joinGameData = new Gson().fromJson(request.body(), JoinGameData.class);

        try {
            service.joinGame(authToken, joinGameData);
            response.status(200);
            String res = "{}";
            response.body(res);
            return res;
        } catch (GameException e) {
            return handleGameException(response, e);
        }
    }

    public Object listGames(Request request, Response response) {
        //
        String authToken = request.headers("authorization");

        try {
            Collection<ListGameData> games = service.listGames(authToken);
            ListData list = new ListData(games);
            response.status(200);
            String res = new Gson().toJson(list);
            response.body(res);
            return res;
        } catch (GameException e) {
            return handleGameException(response, e);
        }
    }
}
