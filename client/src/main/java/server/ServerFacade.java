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
        return makeRequest("POST", path, userData, AuthData.class, null);
    }

    public AuthData login(UserData userData) throws ResponseException{
        var path = "/session";
        return makeRequest("POST", path, userData, AuthData.class, null);
    }

    public void logout(String authToken) throws ResponseException{
        var path = "/session";
        makeRequest("DELETE", path, null, null, authToken);
    }

    public void clear() throws ResponseException{
        var path = "/db";
        makeRequest("DELETE", path, null, null, null);
    }

    public GameData createGame(GameData gameData, String authToken) throws ResponseException{
        var path = "/game";
        return makeRequest("POST", path, gameData, GameData.class, authToken);
    }

    public JoinGameData joinGame(JoinGameData joinGameData, String authToken) throws ResponseException {
        var path = "/game";
        return makeRequest("PUT", path, joinGameData, JoinGameData.class, authToken);
    }

    public ListData listGames(ListGameData listGameData, String authToken) throws ResponseException {
        var path = "/game";
        return makeRequest("GET", path, listGameData, ListData.class, authToken);
    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass,
                              String authToken) throws ResponseException {

        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            if (authToken != null) {
                http.setRequestProperty("authorization", authToken);
            }

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