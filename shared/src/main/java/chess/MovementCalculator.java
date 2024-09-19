package chess;

import java.util.Collection;

public interface MovementCalculator {
    Collection<ChessMove> moves(ChessBoard board, ChessPosition myPosition);
}

