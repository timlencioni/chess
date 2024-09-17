package chess;

import java.util.Collection;

public interface ChessPieceMovement {
    Collection<ChessMove> getPossibleMoves(ChessBoard board, ChessPosition myPosition);
}

public class MovePieces {

    public static class PawnMovement implements ChessPieceMovement {

        @Override
        public Collection<ChessMove> getPossibleMoves(ChessBoard board, ChessPosition myPosition) {

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
    }

    public static class RookMovement implements ChessPieceMovement {
        @Override
        public Collection<ChessMove> getPossibleMoves(ChessBoard board, ChessPosition myPosition) {

            // Return a collection of possible moves
        }
    }

    // TODO:: Implement similar classes for Knight, Bishop, Queen, and King
}