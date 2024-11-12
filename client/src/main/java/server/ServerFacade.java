package server;

import model.*;

import com.google.gson.Gson;
import exception.ResponseException;

import java.io.*;
import java.net.*;

public class ServerFacade {

    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public AuthData register(UserData userData) throws ResponseException{
        var path = "/user";
        return makeRequest("POST", path, userData, AuthData.class);
    }

    public AuthData login(UserData userData) throws ResponseException{
        var path = "/session";
        return makeRequest("POST", path, userData, AuthData.class);
    }

    public void logout() throws ResponseException{
        var path = "/session";
        makeRequest("DELETE", path, null, null);
    }

    public void clear() throws ResponseException{
        var path = "/db";
        makeRequest("DELETE", path, null, null);
    }

    public GameData createGame(GameData gameData) throws ResponseException{
        var path = "/game";
        return makeRequest("POST", path, gameData, GameData.class);
    }

    public JoinGameData joinGame(JoinGameData joinGameData) throws ResponseException {
        var path = "/game";
        return makeRequest("PUT", path, joinGameData, JoinGameData.class);
    }

    public ListGameData listGames(ListGameData listGameData) throws ResponseException {
        var path = "/game";
        return makeRequest("GET", path, listGameData, ListGameData.class);
    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass) throws ResponseException {
        //FIXME:: Add authToken as header;
        // logout, listGames, joinGame
        // setRequestProperty()

        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            writeBody(request, http);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }


    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ResponseException {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            throw new ResponseException(status, "failure: " + status);
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }


    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}