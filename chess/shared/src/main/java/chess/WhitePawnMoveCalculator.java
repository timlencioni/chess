package chess;

import java.util.Collection;
import java.util.HashSet;
// FIXME:: update the direction the pawn is going apparently.
public class WhitePawnMoveCalculator implements MovementCalculator {

    public ChessPiece.PieceType promo = null;

    @Override
    public Collection<ChessMove> moves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> possibleMoves = new HashSet<>();

        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        int rowToAdd = row + 1; // Can only go forward one row

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

        if (row == 2) {
            ChessPosition newPos1 = new ChessPosition(row + 1, col);
            ChessPosition newPos2 = new ChessPosition(row + 2, col);
            if (board.getPiece(newPos1) == null && board.getPiece(newPos2) == null) {
                possibleMoves.add(new ChessMove(myPosition, newPos2, promo));
            }
        }

        return possibleMoves;
    }

    /*public void addMove(Collection<ChessMove> set, ChessPosition currPos, int rowToAdd, int colToAdd,
                        ChessBoard board, ChessPiece.PieceType promo) {
        ChessPosition newPos = new ChessPosition(rowToAdd, colToAdd);
        // Check that we stay on the board
        if (rowToAdd > 0 && colToAdd > 0 && rowToAdd < 9 && colToAdd < 9) {
            //Check that desired position is occupied by someone on the other team
            if (board.getPiece(newPos).getTeamColor() != board.getPiece(currPos).getTeamColor()) {
                set.add(new ChessMove(currPos, new ChessPosition(rowToAdd, colToAdd), promo));
            }
        }
    }*/
}
