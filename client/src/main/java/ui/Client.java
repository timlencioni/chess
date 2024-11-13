package ui;

import static ui.EscapeSequences.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import chess.ChessGame;
import exception.ResponseException;
import model.*;
import server.ServerFacade;

public class Client {

    private static ServerFacade server;
    private boolean LOGGED_IN;
    private String authToken; //FIXME: Does this need to be a Collection?
    private static final String SETUP = SET_TEXT_COLOR_WHITE + SET_BG_COLOR_BLACK;
    private static final String SETUP_ERROR = SET_TEXT_COLOR_RED + SET_BG_COLOR_BLACK;
    private static final String SETUP_SUCCESS = SET_TEXT_COLOR_GREEN + SET_BG_COLOR_BLACK;

    public Client(int port){
        server = new ServerFacade(port);
        LOGGED_IN = false;
        //authTokens = new ArrayList<>();
        authToken = null;
    }

    public String help() {
        if (!LOGGED_IN){
            System.out.println(SETUP + "register <USERNAME> <PASSWORD> <EMAIL> " + SET_TEXT_COLOR_LIGHT_GREY +
                    "- to create an account");
            System.out.println(SETUP + "login <USERNAME> <PASSWORD> " + SET_TEXT_COLOR_LIGHT_GREY +
                    "- to play on existing account");
            System.out.println(SETUP + "quit " + SET_TEXT_COLOR_LIGHT_GREY +
                    "- to stop playing");
            // System.out.println(SETUP + "help " + SET_TEXT_COLOR_LIGHT_GREY + "- to see possible commands");

            return SETUP + "help " + SET_TEXT_COLOR_LIGHT_GREY + "- to see possible commands";
        }
        else {
            System.out.println(SETUP + "logout " + SET_TEXT_COLOR_LIGHT_GREY +
                    "- to end session");
            System.out.println(SETUP + "create <NAME> " + SET_TEXT_COLOR_LIGHT_GREY +
                    "- a new game");
            System.out.println(SETUP + "list " + SET_TEXT_COLOR_LIGHT_GREY +
                    "- all active games");
            System.out.println(SETUP + "join <ID> [WHITE|BLACK] " + SET_TEXT_COLOR_LIGHT_GREY +
                    "- a game as white or black player");
            System.out.println(SETUP + "observe <ID> " + SET_TEXT_COLOR_LIGHT_GREY +
                    "- a game a non-player");
            System.out.println(SETUP + "quit " + SET_TEXT_COLOR_LIGHT_GREY +
                    "- to stop playing");
            // System.out.println(SETUP + "help " + SET_TEXT_COLOR_LIGHT_GREY + "- to see possible commands");

            return SETUP + "help " + SET_TEXT_COLOR_LIGHT_GREY + "- to see possible commands";
        }
    }

    public String eval(String line) {
        try {
            var tokens = line.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            if (!LOGGED_IN) {
                return switch (cmd) {
                    case "register" -> register(params);
                    case "quit" -> "quit";
                    case "login" -> login(params);
                    default -> help();
                };
            }
            else {
                return switch (cmd) {
                    case "logout" -> logout(params);
                    case "create" -> createGame(params);
                    case "list" -> listGames(params);
                    case "join" -> joinGame(params);
                    case "observe" -> observeGame(params);
                    case "quit", "q" -> "quit";
                    default -> help();
                };
            }
        } catch (ResponseException e) {
            return e.getMessage();
        }
    }

    // -------------------- PRELOGIN METHODS -------------------

    private String register(String[] params) throws ResponseException{
        if (params.length != 3) {
            return SETUP_ERROR + "Must have: <USERNAME> <PASSWORD> <EMAIL>";
        }
        else {
            try {
                AuthData authData = server.register(new UserData(params[0], params[1], params[2]));
                authToken = authData.authToken();
            }
            catch (ResponseException e) {
                return e.getMessage();
            }
            LOGGED_IN = true;
            return SETUP_SUCCESS + "Success";
        }
    }

    private String login(String[] params) {
        if (params.length != 2) {
            return SETUP_ERROR + "Must have: <USERNAME> <PASSWORD>";
        }
        else {
            try {
                AuthData authData = server.login(new UserData(params[0], params[1], null));
                authToken = authData.authToken();
            }
            catch (ResponseException e) {
                return e.getMessage();
            }
            LOGGED_IN = true;
            return SETUP_SUCCESS + "Success";
        }
    }

    // ------------------- POSTLOGIN METHODS -------------------

    private String logout(String[] params) {
        if (params.length != 0) {
            return SETUP_ERROR + "No arguments needed!";
        }
        else {
            try {
                server.logout(authToken);
                authToken = null;
            }
            catch (ResponseException e) {
                return e.getMessage();
            }
            LOGGED_IN = false;
            authToken = null;
            return SETUP_SUCCESS + "Success";
        }
    }

    private String createGame(String[] params) {

        if (params.length != 1) {
            return SETUP_ERROR + "Must provide <NAME>!";
        }
        else {
            String name = params[0];
            GameData newGame = new GameData(0, null, null, name, new ChessGame());
            try {
                server.createGame(newGame, authToken);
            }
            catch (ResponseException e) {
                return e.getMessage();
            }

            return SETUP_SUCCESS + "Game Created Successfully";
        }
    }


    private String listGames(String[] params) {
        if (params.length != 0) {
            return SETUP_ERROR + "No arguments needed!";
        }
        else {
            Collection<ListGameData> list;
            try {
                list = server.listGames(authToken).games();
            }
            catch (ResponseException e) {
                return e.getMessage();
            }
            Collection<String> games = new ArrayList<>();
            int listNum = 1;
            System.out.println(SETUP + "Active Games:");
            for (ListGameData game : list) {
                String newString = String.format("%s %s %s %s", listNum, game.gameName(),
                        game.whiteUsername(), game.blackUsername());
                games.add(newString);
                listNum++;
                System.out.println(newString);
            }

            return " ";
        }
    }

    private String joinGame(String[] params) {
        if (params.length != 2) {
            return SETUP_ERROR + "Must provide <ID> [WHITE|BLACK]!";
        }
        else {
            String color = params[1];
            System.out.println(color);
            if (!color.equalsIgnoreCase("WHITE") && !color.equalsIgnoreCase("BLACK")) {
                return SETUP_ERROR + "Team must be WHITE or BLACK";
            }
            JoinGameData joinGame = new JoinGameData(color, Integer.parseInt(params[0]));
            try {
                server.joinGame(joinGame, authToken);
            }
            catch (ResponseException e) {
                return e.getMessage();
            }

            return SETUP_SUCCESS + "Good Luck!";
        }
    }

    private String observeGame(String[] params) {
        if (params.length != 1) {
            return SETUP_ERROR + "Must provide <ID> of game to watch!";
        }
        else {

            drawBoard();

            return SETUP_SUCCESS + "Have fun!";
        }
    }

    // ------------------- MISC. METHODS -------------------
    private void drawBoard() {
        System.out.println(SETUP_ERROR + "Coming soon...");
    }
}
