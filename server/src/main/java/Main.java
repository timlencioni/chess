import chess.*;
import server.Server;

public class Main {
    public static void main(String[] args) {
        server.Server myServer = new server.Server();
        myServer.run(0);

        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Server: " + piece);
    }
}