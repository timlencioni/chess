package chess;

import java.util.Collection;
import java.util.HashSet;

public class QueenMoveCalculator implements MovementCalculator {
    @Override
    public Collection<ChessMove> moves(ChessBoard board, ChessPosition myPosition) {
        // Combine Bishop and Rook Movement Calculators

        // Calculate Bishop Movements
        MovementCalculator bishopCalc = new BishopMoveCalculator();
        Collection <ChessMove> bishopMoves = bishopCalc.moves(board, myPosition);

        // Calculate Rook Movements
        MovementCalculator rookCalc = new RookMoveCalculator();
        Collection <ChessMove> rookMoves = rookCalc.moves(board, myPosition);

        // Combine them into one HashSet
        // source: https://www.geeksforgeeks.org/merge-two-sets-in-java/
        return new HashSet<>() {
            {
                addAll(bishopMoves);
                addAll(rookMoves);
            }
        };
    }
}
