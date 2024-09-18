package chess;

import java.util.Collection;
import java.util.HashSet;

public class RookMoveCalculator implements MovementCalculator {
    @Override
    public Collection<ChessMove> moves(ChessBoard board, ChessPosition myPosition) {
        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        Collection<ChessMove> possibleMoves = new HashSet<>();

        // for loops which runs through all diagonals in a given direction until it ends
        //FIXME: Can we create a function that does the if else statement separately?
        //FIXME: Also fix the Position in getPiece method call
        for (int i = 1; i < 8; i++) { // Up
            int rowToAdd = row + i;
            if (board.getPiece(myPosition) != null && rowToAdd > 0 && rowToAdd < 9) {
                possibleMoves.add(new ChessMove(myPosition, new ChessPosition(rowToAdd, col), null));
            }
            else break;
        }

        for (int i = 1; i < 8; i++) { // Right
            int colToAdd = col + i;
            if (board.getPiece(myPosition) != null && colToAdd > 0 && colToAdd < 9) {
                possibleMoves.add(new ChessMove(myPosition, new ChessPosition(row, colToAdd), null));
            }
            else break;
        }

        for (int i = 1; i < 8; i++) { // Left
            int colToAdd = col - i;
            if (board.getPiece(myPosition) != null && colToAdd > 0 && colToAdd < 9) {
                possibleMoves.add(new ChessMove(myPosition, new ChessPosition(row, colToAdd), null));
            }
            else break;
        }

        for (int i = 1; i < 8; i++) { // Down
            int rowToAdd = row - i;
            if (board.getPiece(myPosition) != null && rowToAdd > 0 && rowToAdd < 9) {
                possibleMoves.add(new ChessMove(myPosition, new ChessPosition(rowToAdd, col), null));
            }
            else break;
        }

        return possibleMoves;
    }
}
