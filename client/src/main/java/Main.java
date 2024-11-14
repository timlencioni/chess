import chess.*;
import ui.Repl;
import static ui.EscapeSequences.*;

public class Main {
    public static void main(String[] args) {
        var port = 0;

        System.out.println(SET_TEXT_COLOR_WHITE + SET_BG_COLOR_BLACK + BLACK_KING + "240 Chess Client:" + WHITE_KNIGHT);

        new Repl(port).run();
    }
}