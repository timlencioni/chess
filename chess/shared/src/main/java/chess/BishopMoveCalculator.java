package chess;

import java.util.Collection;
import java.util.HashSet;

public class BishopMoveCalculator implements MovementCalculator {

    @Override
    public Collection<ChessMove> moves(ChessBoard board, ChessPosition myPosition) {
        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        Collection<ChessMove> possibleMoves = new HashSet<>();

        // for loops which runs through all diagonals in a given direction until it ends
        //FIXME: Can we create a function that does the if else statement separately?
        for (int i = 1; i < 8; i++) { // Up to the right
            int rowToAdd = row + i;
            int colToAdd = col + i;
            ChessPosition newPos = new ChessPosition(rowToAdd, colToAdd);
            if (rowToAdd <= 0 || colToAdd <= 0 || rowToAdd >= 9 || colToAdd >= 9) break;
            if (board.getPiece(newPos) == null) {
                possibleMoves.add(new ChessMove(myPosition, newPos, null));
            }
            else if (board.getPiece(newPos) != null
                    && board.getPiece(newPos).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
                possibleMoves.add(new ChessMove(myPosition, newPos, null));
                break;
            }
            else break;
        }

        for (int i = 1; i < 8; i++) { // Down to the right
            int rowToAdd = row - i;
            int colToAdd = col + i;
            ChessPosition newPos = new ChessPosition(rowToAdd, colToAdd);
            if (rowToAdd <= 0 || colToAdd <= 0 || rowToAdd >= 9 || colToAdd >= 9) break;
            if (board.getPiece(newPos) == null) {
                possibleMoves.add(new ChessMove(myPosition, newPos, null));
            }
            else if (board.getPiece(newPos) != null
                    && board.getPiece(newPos).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
                possibleMoves.add(new ChessMove(myPosition, newPos, null));
                break;
            }
            else break;

        }

        for (int i = 1; i < 8; i++) { // Down to the left
            int rowToAdd = row - i;
            int colToAdd = col - i;
            ChessPosition newPos = new ChessPosition(rowToAdd, colToAdd);
            if (rowToAdd <= 0 || colToAdd <= 0 || rowToAdd >= 9 || colToAdd >= 9) break;
            if (board.getPiece(newPos) == null) {
                possibleMoves.add(new ChessMove(myPosition, newPos, null));
            }
            else if (board.getPiece(newPos) != null
                    && board.getPiece(newPos).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
                possibleMoves.add(new ChessMove(myPosition, newPos, null));
                break;
            }
            else break;
        }

        for (int i = 1; i < 8; i++) { // Up to the left
            int rowToAdd = row + i;
            int colToAdd = col - i;
            ChessPosition newPos = new ChessPosition(rowToAdd, colToAdd);
            if (rowToAdd <= 0 || colToAdd <= 0 || rowToAdd >= 9 || colToAdd >= 9) break;
            if (board.getPiece(newPos) == null) {
                possibleMoves.add(new ChessMove(myPosition, newPos, null));
            }
            else if (board.getPiece(newPos) != null
                    && board.getPiece(newPos).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
                possibleMoves.add(new ChessMove(myPosition, newPos, null));
                break;
            }
            else break;
        }

        return possibleMoves;
    }

    /*private void addMoves(ChessBoard board, ChessPosition myPosition, int rowToAdd, int colToAdd,
                          Collection<ChessMove> possibleMoves) {
        if (board.getPiece(myPosition) != null && rowToAdd > 0 && colToAdd > 0
                && rowToAdd < 9 && colToAdd < 9) {
            possibleMoves.add(new ChessMove(myPosition, new ChessPosition(rowToAdd, colToAdd), null));
        }
        else ;
    }*/


}
