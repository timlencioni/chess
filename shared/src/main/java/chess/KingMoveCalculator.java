package chess;

import java.util.Collection;
import java.util.HashSet;

public class KingMoveCalculator implements MovementCalculator {
    @Override
    public Collection<ChessMove> moves(ChessBoard board, ChessPosition myPosition) {
        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        Collection<ChessMove> possibleMoves = new HashSet<>();

        addMove(possibleMoves, myPosition, row + 1, col + 1, board);
        addMove(possibleMoves, myPosition, row + 1, col, board);
        addMove(possibleMoves, myPosition, row + 1, col - 1, board);

        addMove(possibleMoves, myPosition, row - 1, col + 1, board);
        addMove(possibleMoves, myPosition, row - 1, col, board);
        addMove(possibleMoves, myPosition, row - 1, col - 1, board);

        addMove(possibleMoves, myPosition, row, col + 1, board);
        addMove(possibleMoves, myPosition, row, col - 1, board);

        return possibleMoves; //Return the datastructure with the king moves.
    }

    public void addMove(Collection<ChessMove> set, ChessPosition currPos, int rowToAdd, int colToAdd,
                        ChessBoard board) {
        ChessPosition newPos = new ChessPosition(rowToAdd, colToAdd);
        // Check that we stay on the board
        if (rowToAdd > 0 && colToAdd > 0 && rowToAdd < 9 && colToAdd < 9) {
            //Check that desired position is occupied by someone on the other team
            if (board.getPiece(newPos) != null) {
                if (board.getPiece(newPos).getTeamColor() != board.getPiece(currPos).getTeamColor()) {
                    set.add(new ChessMove(currPos, new ChessPosition(rowToAdd, colToAdd), null));
                }
            }
            else {
                set.add(new ChessMove(currPos, new ChessPosition(rowToAdd, colToAdd), null));
            }
        }
    }
}
