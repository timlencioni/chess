package ui;

import static ui.EscapeSequences.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;
import java.util.Scanner;

import chess.*;
import exception.ResponseException;
import model.*;
import server.ServerFacade;
import server.WebSocketMessenger;

public class Client {

    private static ServerFacade server;
    private boolean loggedIn;
    private boolean inGame;
    private String authToken;
    private String username;
    private String playerColor;
    private int currGameID;
    private static final String SETUP = SET_TEXT_COLOR_WHITE + SET_BG_COLOR_BLACK;
    private static final String SETUP_ERROR = SET_TEXT_COLOR_RED + SET_BG_COLOR_BLACK;
    private static final String SETUP_SUCCESS = SET_TEXT_COLOR_GREEN + SET_BG_COLOR_BLACK;

    private static final int BOARD_SIZE = 8;

    public Client(int port){
        server = new ServerFacade(port);
        server.setClient(this);
        loggedIn = false;
        inGame = false;
        //authTokens = new ArrayList<>();
        authToken = null;
        currGameID = -1;
        username = null;
        playerColor = null;
    }

    public String help() {
        if (!loggedIn){
            System.out.println(SETUP + "register <USERNAME> <PASSWORD> <EMAIL> " + SET_TEXT_COLOR_LIGHT_GREY +
                    "- to create an account");
            System.out.println(SETUP + "login <USERNAME> <PASSWORD> " + SET_TEXT_COLOR_LIGHT_GREY +
                    "- to play on existing account");

        }
        else if (!inGame){
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
        else {
            System.out.println(SETUP + "redraw " + SET_TEXT_COLOR_LIGHT_GREY +
                    "- to redraw chess board");
            System.out.println(SETUP + "leave " + SET_TEXT_COLOR_LIGHT_GREY +
                    "- the game you are in");
            System.out.println(SETUP + "move <MOVE> " + SET_TEXT_COLOR_LIGHT_GREY +
                    "- a piece");
            System.out.println(SETUP + "resign " + SET_TEXT_COLOR_LIGHT_GREY +
                    "- surrender and lose");
            System.out.println(SETUP + "highlight <POSITION> " + SET_TEXT_COLOR_LIGHT_GREY +
                    "- all possible moves for the piece on the given position");
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
                    case "quit", "q" -> "quit";
                    case "login" -> login(params);
                    default -> help();
                };
            }
            else if (!inGame) {
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
            else {
                return switch(cmd) {
                    case "redraw" -> redraw(params);
                    case "leave" -> leave(params);
                    case "move", "m" -> makeMove(params);
                    case "resign" -> resign(params);
                    case "highlight" -> highlightMoves(params);
                    case "quit", "q" -> "quit";
                    default -> help();
                };
            }
        } catch (ResponseException e) {
            return SETUP_ERROR + e.getMessage();
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
                username = authData.username();
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
                return SETUP_ERROR + e.getMessage();
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
                return SETUP_ERROR + e.getMessage();
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
                return SETUP_ERROR + e.getMessage();
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
                return SETUP_ERROR + e.getMessage();
            }
            // Collection<String> games = new ArrayList<>();
            int listNum = 101;
            System.out.println(SETUP + "Active Games:");
            System.out.println(SETUP + SET_TEXT_UNDERLINE + "id \tName \tWhite \tBlack" + RESET_TEXT_UNDERLINE);
            for (ListGameData game : list) {
                String whiteName = game.whiteUsername() == null ? "None" : game.whiteUsername();
                String blackName = game.blackUsername() == null ? "None" : game.blackUsername();
                String newString = String.format("%s \t%s \t%s \t%s", listNum, game.gameName(),
                        whiteName, blackName);
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
            int id;
            try {
                id = Integer.parseInt(params[0]) - 100;
            }
            catch (Exception e) {
                return SETUP_ERROR + "Provide game's list id number!";
            }
            String color = params[1];
            if (!color.equalsIgnoreCase("WHITE") && !color.equalsIgnoreCase("BLACK")) {
                return SETUP_ERROR + "Team must be WHITE or BLACK";
            }
            JoinGameData joinGame = new JoinGameData(color, id);
            try {
                server.joinGame(joinGame, authToken);
            }
            catch (ResponseException e) {
                return SETUP_ERROR + e.getMessage();
            }

            // server.getCurrBoard(id);

            inGame = true;
            currGameID = id;
            playerColor = color;

            return SETUP_SUCCESS + "Good Luck!";
        }
    }

    private String observeGame(String[] params) {
        if (params.length != 1) {
            return SETUP_ERROR + "Must provide <ID> of game to watch!";
        }
        else {
            int id = Integer.parseInt(params[0]) - 100;
            // need to use id to get the correct game to observe
            server.observe(authToken, id);

            inGame = true;
            currGameID = id;

            return SETUP_SUCCESS + "Have fun!";
        }
    }

    // ---------------- GAMEPLAY METHODS -------------------
    private String redraw(String[] params){
        if (params.length != 0) {
            return SETUP_ERROR + "No arguments needed!";
        }

        // get ChessGame
        ChessGame game = server.getCurrGame();

        if (playerColor.equalsIgnoreCase("black")) { drawBoardBlack(game.getBoard()); }
        else { drawBoardWhite(game.getBoard()); }
        return "";
    }

    private String leave(String[] params) {
        if (params.length != 0) {
            return SETUP_ERROR + "No arguments needed!";
        }

        server.leaveGame(authToken, currGameID);

        inGame = false;
        return SETUP_SUCCESS + "Goodbye";
    }

    private String makeMove(String[] params) {
        if (playerColor == null) { return SETUP_ERROR + "Cannot move as observer."; }

        if (params.length >= 2 && params[0].matches("[a-h][1-8]") && params[1].matches("[a-h][1-8]")) {
            ChessPosition currPos = new ChessPosition(params[0].charAt(1) - '0', params[0].charAt(0) - ('a'-1));
            ChessPosition nextPos = new ChessPosition(params[1].charAt(1) - '0', params[1].charAt(0) - ('a'-1));

            ChessPiece.PieceType promotionType = null;
            if (params.length == 3) {
                switch (params[2].toLowerCase()) {
                    case "queen" -> promotionType = ChessPiece.PieceType.QUEEN;
                    case "rook" -> promotionType = ChessPiece.PieceType.ROOK;
                    case "bishop" -> promotionType = ChessPiece.PieceType.BISHOP;
                    case "knight" -> promotionType = ChessPiece.PieceType.KNIGHT;
                    default -> promotionType = null;
                }
                if (promotionType == null) {
                    return SETUP_ERROR + "Invalid Promotion Piece. Try again.";
                }
            }

            ChessMove move = new ChessMove(currPos, nextPos, promotionType);
            server.makeMove(move, authToken, currGameID);
        }
        else {
            System.out.println(SETUP_ERROR + "Invalid Move Command. Should look something like: "
                    + SETUP_SUCCESS + "e2 e4");
        }

        return "";
    }

    private String resign(String[] params) {
        if (params.length != 0) {
            return SETUP_ERROR + "No arguments needed!";
        }
        boolean verify = false;
        while (!verify) {
            System.out.println(SETUP_ERROR + "Are you sure you want to quit? ");
            System.out.print(SET_TEXT_COLOR_WHITE + ">>> ");
            Scanner scanner = new Scanner(System.in);
            String line = scanner.nextLine();
            if (line.equalsIgnoreCase("yes")) {
                server.resign(currGameID, authToken);
                verify = true;
            } else if (line.equalsIgnoreCase("no")) {
                verify = true;
                return "";
            }
        }

        inGame = false; // Do we want this?
        return SETUP_SUCCESS + "You Resigned.";
    }

    private String highlightMoves(String[] params) {
        if (params.length != 1) {
            return SETUP_ERROR + "Must provide <position>!";
        }
        if (!params[0].matches("[a-h][1-8]")) {
            return SETUP_ERROR + "Invalid position format, must be <letter><number>";
        }

        ChessGame game = server.getCurrGame();
        ChessPosition pos = new ChessPosition(params[0].charAt(1) - '0', params[0].charAt(0) - ('a'-1));
        Collection<ChessMove> moves = game.validMoves(pos);

        if (moves == null || moves.isEmpty()) { return SETUP_ERROR + String.format("No possible moves for %s", pos); }

        if (playerColor != null && playerColor.equalsIgnoreCase("black")) {
            drawHighlightBlack(moves, game, pos);
        }
        else {
            drawHighlightWhite(moves, game, pos);
        }


        return "";

    }

    // ------------------- MISC. METHODS -------------------
    private void drawBoardWhite(ChessBoard board) {
        StringBuilder printBoard = new StringBuilder();
        // board.resetBoard();
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
            printBoard.append(RESET_BG_COLOR + SET_TEXT_COLOR_LIGHT_GREY + (8 - i) + "\n");
        }

        printBoard.append(SET_TEXT_COLOR_LIGHT_GREY + "_ a  b  c  d  e  f  g  h _\n");
        System.out.println(printBoard);
    }

    private void drawBoardBlack(ChessBoard board) {
        StringBuilder printBoard = new StringBuilder();
        // board.resetBoard(); // Remove in Phase 6
        ChessPiece[][] squares = board.getSquares();

        printBoard.append(SET_TEXT_COLOR_LIGHT_GREY + "_ h  g  f  e  d  c  b  a _\n");
        for (int i = 0; i < BOARD_SIZE; i++){
            printBoard.append(SET_TEXT_COLOR_LIGHT_GREY);
            printBoard.append(i + 1);
            printBoard.append(SET_TEXT_COLOR_WHITE);
            for (int j = 0; j < BOARD_SIZE; j++){

                setBGColor(printBoard, i, j);
                printBoard.append(getCharacter(squares[7 - i][7 - j]));
            }
            printBoard.append(RESET_BG_COLOR + SET_TEXT_COLOR_LIGHT_GREY + (i + 1) + "\n");
        }

        printBoard.append(SET_TEXT_COLOR_LIGHT_GREY + "_ h  g  f  e  d  c  b  a _\n");
        System.out.println(printBoard);
    }

    public void drawBoard(ChessGame game) {
        System.out.println(ERASE_LINE);
        if (playerColor!= null && playerColor.equalsIgnoreCase("black")) {
            drawBoardBlack(game.getBoard());
        }
        else {
            drawBoardWhite(game.getBoard());
        }
        System.out.print(RESET_TEXT_COLOR + ">>> ");
    }

    private void drawHighlightWhite(Collection<ChessMove> moves, ChessGame game, ChessPosition startPos)
    {
        StringBuilder printBoard = new StringBuilder();
        ChessBoard board = game.getBoard();
        ChessPiece[][] squares = board.getSquares();

        printBoard.append(SET_TEXT_COLOR_LIGHT_GREY + "_ a  b  c  d  e  f  g  h _\n");
        for (int i = 0; i < BOARD_SIZE; i++){
            printBoard.append(SET_TEXT_COLOR_LIGHT_GREY);
            printBoard.append(8 - i);
            printBoard.append(SET_TEXT_COLOR_WHITE);
            for (int j = 0; j < BOARD_SIZE; j++){
                ChessPosition endPos = new ChessPosition(8 - i, j + 1);
                ChessMove move = new ChessMove(startPos, endPos, null);
                if (moves.contains(move) || endPos.equals(startPos)) { printBoard.append(SET_BG_COLOR_YELLOW); }
                else { setBGColor(printBoard, i, j); }

                printBoard.append(getCharacter(squares[i][j]));

            }
            printBoard.append(RESET_BG_COLOR + SET_TEXT_COLOR_LIGHT_GREY + (8 - i) + "\n");
        }

        printBoard.append(SET_TEXT_COLOR_LIGHT_GREY + "_ a  b  c  d  e  f  g  h _\n");
        System.out.println(printBoard);

    }

    private void drawHighlightBlack(Collection<ChessMove> moves, ChessGame game, ChessPosition startPos) {
        StringBuilder printBoard = new StringBuilder();
        ChessBoard board = game.getBoard();
        ChessPiece[][] squares = board.getSquares();

        printBoard.append(SET_TEXT_COLOR_LIGHT_GREY + "_ h  g  f  e  d  c  b  a _\n");
        for (int i = 0; i < BOARD_SIZE; i++){
            printBoard.append(SET_TEXT_COLOR_LIGHT_GREY);
            printBoard.append(i + 1);
            printBoard.append(SET_TEXT_COLOR_WHITE);
            for (int j = 0; j < BOARD_SIZE; j++){
                ChessPosition endPos = new ChessPosition(i + 1, 8 - j);
                ChessMove move = new ChessMove(startPos, endPos, null);
                if (moves.contains(move) || endPos.equals(startPos)) { printBoard.append(SET_BG_COLOR_YELLOW); }
                else { setBGColor(printBoard, i, j); }

                printBoard.append(getCharacter(squares[i][j]));
            }
            printBoard.append(RESET_BG_COLOR + SET_TEXT_COLOR_LIGHT_GREY + (i + 1) + "\n");
        }

        printBoard.append(SET_TEXT_COLOR_LIGHT_GREY + "_ h  g  f  e  d  c  b  a _\n");
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
