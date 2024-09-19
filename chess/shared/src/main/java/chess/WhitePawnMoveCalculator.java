package chess;

import java.util.Collection;
import java.util.HashSet;
// FIXME:: update to account for promotions
public class WhitePawnMoveCalculator implements MovementCalculator {

    @Override
    public Collection<ChessMove> moves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> possibleMoves = new HashSet<>();

        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        int rowToAdd = row + 1; // Can only go forward one row


        for (int i = -1; i < 2; i++) {
            int colToAdd = col + i; //Change column to go forward left, straight, and right.
            ChessPosition newPos = new ChessPosition(rowToAdd, colToAdd);
            if (col != colToAdd && 0 < colToAdd && colToAdd < 8 && board.getPiece(newPos) != null) {
                if (board.getPiece(newPos).getTeamColor() != null
                        && board.getPiece(newPos).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
                    if (row == 7) {
                        addPromoPiece(possibleMoves, myPosition, newPos);
                    } else possibleMoves.add(new ChessMove(myPosition, newPos, null));
                }
            } else if (col == colToAdd && board.getPiece(newPos) == null) {
                if (row == 7) {
                    addPromoPiece(possibleMoves, myPosition, newPos);
                } else possibleMoves.add(new ChessMove(myPosition, newPos, null));
            }
        }


        if (row == 2) {
            ChessPosition newPos1 = new ChessPosition(row + 1, col);
            ChessPosition newPos2 = new ChessPosition(row + 2, col);
            if (board.getPiece(newPos1) == null && board.getPiece(newPos2) == null) {
                possibleMoves.add(new ChessMove(myPosition, newPos2, null));
            }
        }

        return possibleMoves;
    }

    private void addPromoPiece(Collection<ChessMove> set, ChessPosition myPosition, ChessPosition newPos) {
        set.add(new ChessMove(myPosition, newPos, ChessPiece.PieceType.KNIGHT));
        set.add(new ChessMove(myPosition, newPos, ChessPiece.PieceType.QUEEN));
        set.add(new ChessMove(myPosition, newPos, ChessPiece.PieceType.BISHOP));
        set.add(new ChessMove(myPosition, newPos, ChessPiece.PieceType.ROOK));
    }
}
