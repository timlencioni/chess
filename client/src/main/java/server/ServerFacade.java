package server;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.JsonObject;
import model.*;

import com.google.gson.Gson;
import exception.ResponseException;
import ui.Client;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;

import java.io.*;
import java.net.*;

import static ui.EscapeSequences.*;

public class ServerFacade {

    private final int port;
    private WebSocketMessenger ws;
    private ChessGame currGame;
    private Client client;

    public ServerFacade(int port) {

        this.port = port;
        try {
            String url = "http://localhost:" + Integer.toString(port);
            this.ws = new WebSocketMessenger(url);
            this.ws.setServer(this);
        } catch (ResponseException e) {
            System.out.println(SET_TEXT_COLOR_RED + "Failed to make connection with Server.");
        }

        currGame = null;
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
        JoinGameData joinData = makeRequest("PUT", path, joinGameData, JoinGameData.class, authToken);
        wsJoinGame(joinGameData.gameID(), joinGameData.playerColor(), authToken);
        return joinData;
    }

    public ListData listGames(String authToken) throws ResponseException {
        var path = "/game";
        return makeRequest("GET", path, null, ListData.class, authToken);
    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass,
                              String authToken) throws ResponseException {

        try {
            URL url = (new URI("http://localhost:" + port + path)).toURL();
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
            InputStreamReader reader = new InputStreamReader(http.getErrorStream());
            InputStream error = http.getErrorStream();
            BufferedReader bufferedReader = new BufferedReader(reader);
            StringBuilder errorMessage = new StringBuilder();
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                errorMessage.append(line).append("\n");
            }
            reader.close();

            JsonObject jsonObject = new Gson().fromJson(errorMessage.toString(), JsonObject.class);

            // System.out.println(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_RED);
            throw new ResponseException(status, jsonObject.get("message").getAsString());
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

    // wsJoin returns the game HOW???
    public void wsJoinGame(int gameID, String color, String authToken) {
        String message = new Gson().toJson(new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID));
        ws.session.getAsyncRemote().sendText(message);
    }

    public void leaveGame(String authToken, int gameID) {
        String message = new Gson().toJson(new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID));
        ws.session.getAsyncRemote().sendText(message);
    }

    public void resign(int gameID, String authToken) {
        String message = new Gson().toJson(new UserGameCommand(UserGameCommand.CommandType.RESIGN, authToken, gameID));
        ws.session.getAsyncRemote().sendText(message);
    }

    public void setClient(Client client) { this.client = client; }

    public void setCurrGame(ChessGame game) {
        currGame = game;
        client.drawBoard(game);
    }

    public ChessGame getCurrGame() { return currGame; }

    public void observe(String authToken, int gameID) {
        String message = new Gson().toJson(new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID));
        ws.session.getAsyncRemote().sendText(message);
    }

    public void makeMove(ChessMove move, String authToken, int gameID) {
        String message = new Gson().toJson(new MakeMoveCommand(authToken, gameID, move));
        ws.session.getAsyncRemote().sendText(message);
    }
}