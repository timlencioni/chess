package chess;

import java.util.Collection;
import java.util.HashSet;

public class KnightMoveCalculator implements MovementCalculator {
    @Override
    public Collection<ChessMove> moves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> possibleMoves = new HashSet<>();

        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        for (int i = -1; i < 2; i++) {
            if (i == 0) continue;
            int rowToAdd = row + i;
            int colToAdd = col + 2;
            ChessPosition newPos = new ChessPosition(rowToAdd, colToAdd);

            if (rowToAdd > 0 && rowToAdd < 9 && colToAdd > 0 && colToAdd < 9) {
                addMove(board, myPosition, newPos, possibleMoves);
            }

            colToAdd = col - 2;
            newPos = new ChessPosition(rowToAdd, colToAdd);

            if (rowToAdd > 0 && rowToAdd < 9 && colToAdd > 0 && colToAdd < 9) {
                addMove(board, myPosition, newPos, possibleMoves);
            }
        }

        for (int i = -1; i < 2; i++){
            if (i == 0) continue;
            int rowToAdd = row + 2;
            int colToAdd = col + i;
            ChessPosition newPos = new ChessPosition(rowToAdd, colToAdd);

            if (rowToAdd > 0 && rowToAdd < 9 && colToAdd > 0 && colToAdd < 9) {
                addMove(board, myPosition, newPos, possibleMoves);
            }

            if (row - 2 < 0) continue;
            rowToAdd = row - 2;
            newPos = new ChessPosition(rowToAdd, colToAdd);

            if (rowToAdd > 0 && rowToAdd < 9 && colToAdd > 0 && colToAdd < 9) {
                addMove(board, myPosition, newPos, possibleMoves);
            }
        }
        return possibleMoves;
    }

    private void addMove(ChessBoard board, ChessPosition myPosition,
                         ChessPosition newPos, Collection<ChessMove> possibleMoves) {
        if (board.getPiece(newPos) == null) {
            possibleMoves.add(new ChessMove(myPosition, newPos, null));
        } else if (board.getPiece(newPos).getTeamColor() != board.getPiece(myPosition).getTeamColor()){
            possibleMoves.add(new ChessMove(myPosition, newPos, null));
        }
    }


}
