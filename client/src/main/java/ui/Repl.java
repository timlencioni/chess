package ui;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Repl {

    private static Client client;

    public Repl(int port) {
        client = new Client(port);
    }

    public void run() {
        System.out.println("\uD83D\uDC36 Welcome to Chess. Enter 'help' to get started.");
        System.out.print(client.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = client.eval(line);
                System.out.print(SET_TEXT_COLOR_WHITE + result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }

    private void printPrompt() {
        System.out.print("\n" + RESET_TEXT_COLOR + RESET_BG_COLOR + ">>> " + SET_TEXT_COLOR_GREEN);
    }
}
