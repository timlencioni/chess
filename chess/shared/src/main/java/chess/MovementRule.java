package chess;

import java.util.Collection;

public interface MovementRule {
    public Collection<ChessMove> moves(ChessBoard board, ChessPosition myPosition);
}


/*
public abstract class BaseMovement implements MovementRule {

    @Override
    protected void Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition, int row, int col,
                                                        Collection<ChessMove> moves, boolean allowDistance) {
        // TODO:: Calculate most piece rules.
        // First step, look at proposed direction to move to.
        // Check if their are pieces in the way
        // Add new position to list of potential moves.
    }

    public abstract Collection<ChessMove> moves(ChessBoard board, ChessPosition myPosition);
}

public class BishopMovement extends BaseMovement {
    @Override
    public Collection<ChessMove> moves(ChessBoard board, ChessPosition myPosition) {
        var moves = new HashSet<ChessMove>();
        calculateMoves(board, myPosition, -1, -1, moves, true);
        calculateMoves(board, myPosition, -1,  1, moves, true);
        calculateMoves(board, myPosition,  1, -1, moves, true);
        calculateMoves(board, myPosition,  1,  1, moves, true);
        return moves;
    }
}

public class KingMovement extends BaseMovement {
    @Override
    public Collection<ChessMove> moves(ChessBoard board, ChessPosition myPosition) {
        var moves = new HashSet<ChessMove>();
        calculateMoves(board, myPosition,  0,  1, moves, false);
        calculateMoves(board, myPosition,  0, -1, moves, false);
        calculateMoves(board, myPosition,  1,  0, moves, false);
        calculateMoves(board, myPosition, -1,  0, moves, false);
        calculateMoves(board, myPosition, -1, -1, moves, false);
        calculateMoves(board, myPosition, -1,  1, moves, false);
        calculateMoves(board, myPosition,  1, -1, moves, false);
        calculateMoves(board, myPosition,  1,  1, moves, false);
        return moves;
    }
}

public class QueenMovement extends BaseMovement {
    @Override
    public Collection<ChessMove> moves(ChessBoard board, ChessPosition myPosition) {
        var moves = new HashSet<ChessMove>();
        calculateMoves(board, myPosition,  0,  1, moves, true);
        calculateMoves(board, myPosition,  0, -1, moves, true);
        calculateMoves(board, myPosition,  1,  0, moves, true);
        calculateMoves(board, myPosition, -1,  0, moves, true);
        calculateMoves(board, myPosition, -1, -1, moves, true);
        calculateMoves(board, myPosition, -1,  1, moves, true);
        calculateMoves(board, myPosition,  1, -1, moves, true);
        calculateMoves(board, myPosition,  1,  1, moves, true);
        return moves;
    }
}

public class RookMovement extends BaseMovement {
    @Override
    public Collection<ChessMove> moves(ChessBoard board, ChessPosition myPosition) {
        var moves = new HashSet<ChessMove>();
        calculateMoves(board, myPosition,  0,  1, moves, true);
        calculateMoves(board, myPosition,  0, -1, moves, true);
        calculateMoves(board, myPosition,  1,  0, moves, true);
        calculateMoves(board, myPosition, -1,  0, moves, true);
        return moves;
    }
}

public class PawnMovement extends BaseMovement {
    @Override
    public Collection<ChessMove> moves(ChessBoard board, ChessPosition myPosition) {
        var moves = new HashSet<ChessMove>();
        calculateMoves(board, myPosition,  1,  0, moves, false);
        calculateMoves(board, myPosition,  1, -1, moves, false);
        calculateMoves(board, myPosition,  1,  1, moves, false);
        return moves;
    }
}


/*
{

        Collection<ChessMove> possibleMoves = new HashSet<ChessMove>;
        int currRow = myPosition.getRow();
        int currCol = myPosition.getColumn();

        // Return a collection of possible moves

        if (ChessBoard.getPiece(currRow + 1, currCol) == null) {
            possibleMoves.add(ChessMove(myPosition, ChessPosition(currRow + 1, currCol)));
        }
        if (currRow == 2 && ChessBoard.getPiece(currRow + 1, currCol) == null &&
                ChessBoard.getPiece(currRow + 2, currCol) == null) {
            possibleMoves.add(ChessMove(myPosition, ChessPosition(currRow + 2, currCol)));
        }
        if (ChessBoard.getPiece(currRow + 1, currCol + 1) != null) {
            possibleMoves.add(ChessMove(myPosition, ChessPosition(currRow + 1, currCol + 1)));
        }
        if (ChessBoard.getPiece(currRow + 1, currCol - 1) != null) {
            possibleMoves.add(ChessMove(myPosition, ChessPosition(currRow + 1, currCol)));
        }

        return possibleMoves;
    }
 */
