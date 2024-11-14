package ui;

import static ui.EscapeSequences.*;

import java.util.Arrays;
import java.util.Collection;

import chess.*;
import exception.ResponseException;
import model.*;
import server.ServerFacade;

public class Client {

    private static ServerFacade server;
    private boolean loggedIn;
    private String authToken;
    private static final String SETUP = SET_TEXT_COLOR_WHITE + SET_BG_COLOR_BLACK;
    private static final String SETUP_ERROR = SET_TEXT_COLOR_RED + SET_BG_COLOR_BLACK;
    private static final String SETUP_SUCCESS = SET_TEXT_COLOR_GREEN + SET_BG_COLOR_BLACK;

    private static final int BOARD_SIZE = 8;

    public Client(int port){
        server = new ServerFacade(port);
        loggedIn = false;
        //authTokens = new ArrayList<>();
        authToken = null;
    }

    public String help() {
        if (!loggedIn){
            System.out.println(SETUP + "register <USERNAME> <PASSWORD> <EMAIL> " + SET_TEXT_COLOR_LIGHT_GREY +
                    "- to create an account");
            System.out.println(SETUP + "login <USERNAME> <PASSWORD> " + SET_TEXT_COLOR_LIGHT_GREY +
                    "- to play on existing account");

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

        }
        System.out.println(SETUP + "quit " + SET_TEXT_COLOR_LIGHT_GREY +
                "- to stop playing");
        return SETUP + "help " + SET_TEXT_COLOR_LIGHT_GREY + "- to see possible commands";
    }

    public String eval(String line) {
        try {
            var tokens = line.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            if (!loggedIn) {
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
            loggedIn = true;
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
            loggedIn = true;
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
            }
            catch (ResponseException e) {
                return e.getMessage();
            }

            loggedIn = false;
            authToken = null; // Delete aut
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
            // Collection<String> games = new ArrayList<>();
            int listNum = 1;
            System.out.println(SETUP + "Active Games:");
            for (ListGameData game : list) {
                String newString = String.format("%s %s %s %s", listNum, game.gameName(),
                        game.whiteUsername(), game.blackUsername());
                // games.add(newString);
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
            int id = Integer.parseInt(params[0]);
            String color = params[1];
            if (!color.equalsIgnoreCase("WHITE") && !color.equalsIgnoreCase("BLACK")) {
                return SETUP_ERROR + "Team must be WHITE or BLACK";
            }
            JoinGameData joinGame = new JoinGameData(color, id);
            try {
                server.joinGame(joinGame, authToken);
            }
            catch (ResponseException e) {
                return e.getMessage();
            }

            if (color.equalsIgnoreCase("white")){ drawBoardWhite(new ChessBoard()); }
            else { drawBoardBlack(new ChessBoard()); }

            return SETUP_SUCCESS + "Good Luck!";
        }
    }

    private String observeGame(String[] params) {
        if (params.length != 1) {
            return SETUP_ERROR + "Must provide <ID> of game to watch!";
        }
        else {
            int id = Integer.parseInt(params[0]);
            // need to use id to get the correct game to observe

            drawBoardWhite(new ChessBoard());

            return SETUP_SUCCESS + "Have fun!";
        }
    }

    // ------------------- MISC. METHODS -------------------
    private void drawBoardWhite(ChessBoard board) {
        StringBuilder printBoard = new StringBuilder();
        board.resetBoard();
        ChessPiece[][] squares = board.getSquares();

        printBoard.append(SET_TEXT_COLOR_LIGHT_GREY + "_ a  b  c  d  e  f  g  h _\n");
        for (int i = 0; i < BOARD_SIZE; i++){
            printBoard.append(SET_TEXT_COLOR_LIGHT_GREY);
            printBoard.append(8 - i);
            printBoard.append(SET_TEXT_COLOR_WHITE);
            for (int j = 0; j < BOARD_SIZE; j++){
                setBGColor(printBoard, i, j);

                printBoard.append(getCharacter(squares[i][j]));
            }
            printBoard.append(RESET_BG_COLOR + "\n");
        }

        System.out.println(printBoard);
    }

    private void drawBoardBlack(ChessBoard board) {
        StringBuilder printBoard = new StringBuilder();
        board.resetBoard(); // Remove in Phase 6
        ChessPiece[][] squares = board.getSquares();

        printBoard.append(SET_TEXT_COLOR_LIGHT_GREY + "_ h  g  f  e  d  c  b  a _\n");
        for (int i = 0; i < BOARD_SIZE; i++){
            printBoard.append(SET_TEXT_COLOR_LIGHT_GREY);
            printBoard.append(i + 1);
            printBoard.append(SET_TEXT_COLOR_WHITE);
            for (int j = 0; j < BOARD_SIZE; j++){

                setBGColor(printBoard, i, j);
                printBoard.append(getCharacter(squares[7 - i][j]));
            }
            printBoard.append(RESET_BG_COLOR + "\n");
        }

        System.out.println(printBoard);
    }

    private void setBGColor(StringBuilder printBoard, int i, int j) {
        int row = i % 2;
        int col = j % 2;

        if (row == 0 && col == 0) { printBoard.append(SET_BG_COLOR_GREEN); }
        else if (row == 0 && col == 1) { printBoard.append(SET_BG_COLOR_DARK_GREEN); }
        else if (row == 1 && col == 0) { printBoard.append(SET_BG_COLOR_DARK_GREEN); }
        else if (row == 1 && col == 1) { printBoard.append(SET_BG_COLOR_GREEN); }
    }

    private String getCharacter(ChessPiece chessPiece) {
        if (chessPiece == null) { return "   "; }
        ChessPiece.PieceType type = chessPiece.getPieceType();
        ChessGame.TeamColor pieceColor = chessPiece.getTeamColor();
        if (type == ChessPiece.PieceType.KING && pieceColor == ChessGame.TeamColor.WHITE) { return WHITE_KING; }
        else if (type == ChessPiece.PieceType.KING && pieceColor == ChessGame.TeamColor.BLACK) { return BLACK_KING; }
        else if (type == ChessPiece.PieceType.QUEEN && pieceColor == ChessGame.TeamColor.WHITE) { return WHITE_QUEEN; }
        else if (type == ChessPiece.PieceType.QUEEN && pieceColor == ChessGame.TeamColor.BLACK) { return BLACK_QUEEN; }
        else if (type == ChessPiece.PieceType.BISHOP && pieceColor == ChessGame.TeamColor.WHITE) { return WHITE_BISHOP; }
        else if (type == ChessPiece.PieceType.BISHOP && pieceColor == ChessGame.TeamColor.BLACK) { return BLACK_BISHOP; }
        else if (type == ChessPiece.PieceType.KNIGHT && pieceColor == ChessGame.TeamColor.WHITE) { return WHITE_KNIGHT; }
        else if (type == ChessPiece.PieceType.KNIGHT && pieceColor == ChessGame.TeamColor.BLACK) { return BLACK_KNIGHT; }
        else if (type == ChessPiece.PieceType.ROOK && pieceColor == ChessGame.TeamColor.WHITE) { return WHITE_ROOK; }
        else if (type == ChessPiece.PieceType.ROOK && pieceColor == ChessGame.TeamColor.BLACK) { return BLACK_ROOK; }
        else if (type == ChessPiece.PieceType.PAWN && pieceColor == ChessGame.TeamColor.WHITE) { return WHITE_PAWN; }
        else if (type == ChessPiece.PieceType.PAWN && pieceColor == ChessGame.TeamColor.BLACK) {return BLACK_PAWN; }
        else { return "  "; }
    }
}
