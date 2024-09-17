package chess;

/**
 * Represents moving a chess piece on a chessboard
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessMove {

    private ChessPosition start;
    private ChessPosition end;
    private ChessPiece.PieceType promotionPiece;

    public ChessMove(ChessPosition startPosition, ChessPosition endPosition,
                     ChessPiece.PieceType promotionPiece) {

        this.start = startPosition;
        this.end = endPosition;
        this.promotionPiece = promotionPiece;
    }

    /**
     * @return ChessPosition of starting location
     */
    public ChessPosition getStartPosition() {
        return this.start;
    }

    /**
     * @return ChessPosition of ending location
     */
    public ChessPosition getEndPosition() {
        return this.end;
    }

    /**
     * Gets the type of piece to promote a pawn to if pawn promotion is part of this
     * chess move
     *
     * @return Type of piece to promote a pawn to, or null if no promotion
     */
    public ChessPiece.PieceType getPromotionPiece() {
        return this.promotionPiece;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessMove move = (ChessMove) o;
        return (start.equals(move.start) && end.equals(move.end)
                && promotionPiece == move.promotionPiece);

    }

    @Override
    public int hashCode() {
        var promotionCode = (promotionPiece == null ?
                9 : promotionPiece.ordinal());
        return (71 * start.hashCode()) + end.hashCode() + promotionCode;
    }

    @Override
    public String toString() {
        var p = (promotionPiece == null ? "" : ":" + promotionPiece);
        return String.format("%s:%s%s", start.toString(), end.toString(), p);
    }

}
