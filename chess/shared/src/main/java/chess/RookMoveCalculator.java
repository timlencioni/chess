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
        for (int i = 1; i < 8; i++) { // Up
            int rowToAdd = row + i;
            ChessPosition newPos = new ChessPosition(rowToAdd, col);
            if (rowToAdd > 0 && rowToAdd < 9) {
                if (board.getPiece(newPos) != null
                        && board.getPiece(newPos).getTeamColor() == board.getPiece(myPosition).getTeamColor()) {
                    break;
                }
                else if (board.getPiece(newPos) != null
                        && board.getPiece(newPos).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
                    possibleMoves.add(new ChessMove(myPosition, new ChessPosition(rowToAdd, col), null));
                    break;
                }
                else possibleMoves.add(new ChessMove(myPosition, new ChessPosition(rowToAdd, col), null));
            }
        }

        for (int i = 1; i < 8; i++) { // Right
            int colToAdd = col + i;
            ChessPosition newPos = new ChessPosition(row, colToAdd);
            if (colToAdd > 0 && colToAdd < 9) {
                if (board.getPiece(newPos) != null
                        && board.getPiece(newPos).getTeamColor() == board.getPiece(myPosition).getTeamColor()) {
                    break;
                }
                else if (board.getPiece(newPos) != null
                        && board.getPiece(newPos).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
                    possibleMoves.add(new ChessMove(myPosition, new ChessPosition(row, colToAdd), null));
                    break;
                }
                else possibleMoves.add(new ChessMove(myPosition, new ChessPosition(row, colToAdd), null));
            }
        }

        for (int i = 1; i < 8; i++) { // Left
            int colToAdd = col - i;
            ChessPosition newPos = new ChessPosition(row, colToAdd);
            if (colToAdd > 0 && colToAdd < 9) {
                if (board.getPiece(newPos) != null
                        && board.getPiece(newPos).getTeamColor() == board.getPiece(myPosition).getTeamColor()) {
                    break;
                }
                else if (board.getPiece(newPos) != null
                        && board.getPiece(newPos).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
                    possibleMoves.add(new ChessMove(myPosition, new ChessPosition(row, colToAdd), null));
                    break;
                }
                else possibleMoves.add(new ChessMove(myPosition, new ChessPosition(row, colToAdd), null));
            }
        }

        for (int i = 1; i < 8; i++) { // Down
            int rowToAdd = row - i;
            ChessPosition newPos = new ChessPosition(rowToAdd, col);
            if (rowToAdd > 0 && rowToAdd < 9) {
                if (board.getPiece(newPos) != null
                        && board.getPiece(newPos).getTeamColor() == board.getPiece(myPosition).getTeamColor()) {
                    break;
                }
                else if (board.getPiece(newPos) != null
                        && board.getPiece(newPos).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
                    possibleMoves.add(new ChessMove(myPosition, new ChessPosition(rowToAdd, col), null));
                    break;
                }
                else possibleMoves.add(new ChessMove(myPosition, new ChessPosition(rowToAdd, col), null));
            }
        }

        return possibleMoves;
    }
}
