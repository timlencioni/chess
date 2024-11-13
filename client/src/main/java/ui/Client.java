package ui;

import static ui.EscapeSequences.*;
import java.util.Arrays;

import exception.ResponseException;
import server.ServerFacade;

public class Client {

    private static ServerFacade server;
    private static String SETUP = SET_TEXT_COLOR_WHITE + SET_BG_COLOR_BLACK;

    public Client(int port){
        server = new ServerFacade(port);
    }

    public String help() {
        System.out.println(SETUP + "register <USERNAME> <PASSWORD> <EMAIL> " + SET_TEXT_COLOR_LIGHT_GREY +
                "- to create an account");
        System.out.println(SETUP + "login <USERNAME> <PASSWORD> " + SET_TEXT_COLOR_LIGHT_GREY +
                "- to play on existing account");
        System.out.println(SETUP + "quit " + SET_TEXT_COLOR_LIGHT_GREY +
                "- to stop playing");
        // System.out.println(SETUP + "help " + SET_TEXT_COLOR_LIGHT_GREY + "- to see possible commands");

        return SETUP + "help " + SET_TEXT_COLOR_LIGHT_GREY + "- to see possible commands";
    }

    public String eval(String line) {
        try {
            var tokens = line.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "register" -> register(params);

                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    private String register(String[] params) throws ResponseException{
        return SETUP + "FIXME";
    }
}
