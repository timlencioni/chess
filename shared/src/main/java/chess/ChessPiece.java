package chess;

import java.util.Collection;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private ChessPiece.PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return this.pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return this.type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        switch (this.type){
            case KING :
                MovementCalculator kingMoves = new KingMoveCalculator();
                return kingMoves.moves(board, myPosition);
                // break;
            case BISHOP:
                MovementCalculator bishopMoves = new BishopMoveCalculator();
                return bishopMoves.moves(board, myPosition);
            case ROOK:
                MovementCalculator rookMoves = new RookMoveCalculator();
                return rookMoves.moves(board, myPosition);
            case QUEEN:
                MovementCalculator queenMoves = new QueenMoveCalculator();
                return queenMoves.moves(board, myPosition);
            case PAWN:
                if (pieceColor == ChessGame.TeamColor.WHITE) {
                    MovementCalculator pawnMoves = new WhitePawnMoveCalculator();
                    return pawnMoves.moves(board, myPosition);
                }
                else {
                    MovementCalculator pawnMoves = new BlackPawnMoveCalculator();
                    return pawnMoves.moves(board, myPosition);
                }
            case KNIGHT:
                MovementCalculator knightMoves = new KnightMoveCalculator();
                return knightMoves.moves(board, myPosition);
            default:
                System.out.println("Invalid Piece Type");
                return null;
        }

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPiece piece = (ChessPiece) o;

        return pieceColor.equals(piece.pieceColor) && type.equals(piece.type);
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 71 * result + (pieceColor != null ? pieceColor.hashCode() : 0);
        result = 71 * result + (type != null ? type.hashCode() : 0);
        return result;
    }

    @Override
    public String toString(){
        if (type == PieceType.KING && pieceColor == ChessGame.TeamColor.WHITE) return "K";
        else if (type == PieceType.KING && pieceColor == ChessGame.TeamColor.BLACK) return "k";
        else if (type == PieceType.QUEEN && pieceColor == ChessGame.TeamColor.WHITE) return "Q";
        else if (type == PieceType.QUEEN && pieceColor == ChessGame.TeamColor.BLACK) return "q";
        else if (type == PieceType.BISHOP && pieceColor == ChessGame.TeamColor.WHITE) return "B";
        else if (type == PieceType.BISHOP && pieceColor == ChessGame.TeamColor.BLACK) return "b";
        else if (type == PieceType.KNIGHT && pieceColor == ChessGame.TeamColor.WHITE) return "N";
        else if (type == PieceType.KNIGHT && pieceColor == ChessGame.TeamColor.BLACK) return "n";
        else if (type == PieceType.ROOK && pieceColor == ChessGame.TeamColor.WHITE) return "R";
        else if (type == PieceType.ROOK && pieceColor == ChessGame.TeamColor.BLACK) return "r";
        else if (type == PieceType.PAWN && pieceColor == ChessGame.TeamColor.WHITE) return "P";
        else if (type == PieceType.PAWN && pieceColor == ChessGame.TeamColor.BLACK) return "p";
        else return " ";
    }

    public void setType(PieceType type) {
        this.type = type;
    }
}
