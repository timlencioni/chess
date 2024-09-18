package chess;

import java.util.Collection;
import java.util.HashSet;

public class BlackPawnMoveCalculator implements MovementCalculator {

    public ChessPiece.PieceType promo = null;

    @Override
    public Collection<ChessMove> moves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> possibleMoves = new HashSet<>();

        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        int rowToAdd = row - 1; // Can only go forward one row

        if (row < 8) { // If the pawn is on the end, we can't do anything, and it should be promoted anyway
            for (int i = -1; i < 2; i++) {
                int colToAdd = col + i; //Change column to go forward left, straight, and right.
                ChessPosition newPos = new ChessPosition(rowToAdd, colToAdd);
                if (col != colToAdd) {
                    if (0 < colToAdd && colToAdd < 8) {
                        if (board.getPiece(newPos) != null) {
                            if (board.getPiece(newPos).getTeamColor() != null
                                    && board.getPiece(newPos).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
                                possibleMoves.add(new ChessMove(myPosition, newPos, promo));
                            }
                        }
                    }
                } else {
                    if (board.getPiece(newPos) == null) {
                        possibleMoves.add(new ChessMove(myPosition, newPos, promo));
                    }
                }
            }
        }

        if (row == 7) {
            ChessPosition newPos1 = new ChessPosition(row - 1, col);
            ChessPosition newPos2 = new ChessPosition(row - 2, col);
            if (board.getPiece(newPos1) == null && board.getPiece(newPos2) == null) {
                possibleMoves.add(new ChessMove(myPosition, newPos2, promo));
            }
        }

        return possibleMoves;
    }
}
