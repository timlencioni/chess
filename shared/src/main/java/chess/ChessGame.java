package chess;

import java.util.Collection;
import java.util.HashSet;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private TeamColor teamTurn;
    private ChessBoard board = new ChessBoard();

    public ChessGame() {

        this.teamTurn = TeamColor.WHITE;
        this.board.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return this.teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) { this.teamTurn = team; }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);
        if (piece == null) {
            return null;
        }
        Collection<ChessMove> allMoves = piece.pieceMoves(getBoard(), startPosition);
        Collection<ChessMove> onlyValid = new HashSet<>();

        for (ChessMove move : allMoves) {
            ChessGame testGame = new ChessGame(); // this.deepCopy();
            testGame.setBoard(this.board.deepCopy());

            try {
                testGame.testMove(testGame.getBoard(), move);
                if (!testGame.isInCheck(piece.getTeamColor())) {
                    onlyValid.add(move);
                }

            } catch (InvalidMoveException e) {
                System.out.println(e);
                System.out.println(move);
            }
        }

        return onlyValid;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition start = move.getStartPosition();
        ChessPosition end = move.getEndPosition();
        ChessPiece.PieceType promotion = move.getPromotionPiece();

        if (this.board.getPiece(start) == null) {
            throw new InvalidMoveException("No piece at Start Position");
        }
        if (this.board.getPiece(start).getTeamColor() != this.teamTurn) {
            throw new InvalidMoveException("Not your turn");
        }

        Collection<ChessMove> valids = validMoves(start);

        ChessPiece[][] newSquares = this.board.getSquares();

        if (valids.contains(move)){
            ChessPiece newPiece = this.board.getPiece(start);

            if (this.board.getPiece(start).getPieceType() == ChessPiece.PieceType.PAWN
                    && (start.getRow() == 7 || start.getRow() == 2) && promotion != null) {
                newPiece.setType(promotion);
            }

            newSquares[8 - end.getRow()][end.getColumn() - 1] = newPiece;
            newSquares[8 - start.getRow()][start.getColumn() - 1] = null;

        }
        else {
            throw new InvalidMoveException();
        }

        if (this.teamTurn == TeamColor.WHITE) {
            this.teamTurn = TeamColor.BLACK;
        }
        else {
            this.teamTurn = TeamColor.WHITE;
        }

    }

    /**
     * Makes a move in a virtual (test) chess game  for validMove to use to verify the validity of the given move
     *
     * @param myBoard a ChessBoard object to manipulate separate from the actual game
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public ChessBoard testMove(ChessBoard myBoard, ChessMove move) throws InvalidMoveException {
        ChessPosition start = move.getStartPosition();
        ChessPosition end = move.getEndPosition();

        if (myBoard.getPiece(start) == null) {
            throw new InvalidMoveException("No piece at Start Position");
        }

        ChessPiece[][] newSquares = myBoard.getSquares();

        newSquares[8 - end.getRow()][end.getColumn() - 1] = myBoard.getPiece(start);
        newSquares[8 - start.getRow()][start.getColumn() - 1] = null;

        return myBoard;
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPiece[][] squares = board.getSquares();
        // Find the King's position to verify check
        ChessPosition kingPos = null;
        for (int i = 0; i < squares.length; i++) {
            for (int j = 0; j < squares[i].length; j++) {
                if (squares[i][j] != null && squares[i][j].getTeamColor() == teamColor
                    && squares[i][j].getPieceType() == ChessPiece.PieceType.KING) {
                    kingPos = new ChessPosition(8 - i, j + 1);
                    break;
                }
            }
            if (kingPos != null) {
                break;
            }
        }

        // Verify all moves that end with the position of the teamColor's King
        for (int i = 0; i < squares.length; i++) {
            for (int j = 0; j < squares[i].length; j++) {
                if (isOpponentPiece(squares[i][j], teamColor)) {
                    if (canPieceAttackKing(squares[i][j], board, kingPos, i, j)) {
                        return true;
                    }
                }
            }
        }
        
        return false; //default case
    }

    private boolean canPieceAttackKing(ChessPiece chessPiece, ChessBoard board, ChessPosition kingPos, int i, int j) {
        Collection<ChessMove> moves = chessPiece.pieceMoves(board, new ChessPosition(8 - i, j + 1));
        for (ChessMove move : moves) {
            if (move.getEndPosition().equals(kingPos)) {
                return true;
            }
        }
        return false;
    }

    private boolean isOpponentPiece(ChessPiece chessPiece, TeamColor teamColor) {
        return chessPiece != null && chessPiece.getTeamColor() != teamColor;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        // First verify isInCheck...
        ChessPiece[][] squares = board.getSquares();
        if (!isInCheck(teamColor)) {
            return false;
        }

        return verifyMoves(teamColor, squares);
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (isInCheck(teamColor)) {
            return false;
        }

        ChessPiece[][] squares = board.getSquares();

        return verifyMoves(teamColor, squares);
    }

    private boolean verifyMoves(TeamColor teamColor, ChessPiece[][] squares) {
        for (int i = 0; i < squares.length; i++) {
            for (int j = 0; j < squares[i].length; j++) {
                ChessPosition pos = new ChessPosition(8-i, j+1);
                Collection<ChessMove> tempValids = validMoves(pos);
                if (tempValids != null && board.getPiece(pos).getTeamColor() == teamColor && !tempValids.isEmpty()) {
                    return false;
                }

            }
        }

        return true;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {

        this.board = board;

    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return this.board;
    }

}
