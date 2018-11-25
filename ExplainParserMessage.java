public class ExplainParserMessage {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BOLD = "\u001B[1m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    public static int warningCount = 0;
    public static int errorCount = 0;

    private static void stderrLn(String message) {
        System.err.println(message);
    }

    private static void stderr(String message) {
        System.err.print(message);
    }

    public static void die() {
        System.exit(1);
    }

    public static void sendUnqualifiedErrorPrefix() {
        stderr("explain: ");
    }

    public static void sendLocation(String filename) {
        String message = ANSI_BOLD + filename + ": " + ANSI_RESET;

        stderr(message);
    }

    public static void sendLocation(String filename, int line, int column) {
        String message = ANSI_BOLD + filename + ":" + line + ":" + column + ": " + ANSI_RESET;

        stderr(message);
    }

    public static void send(String message, ExplainSeverityLevel level) {
        String messagePrefix = "";
        Boolean shouldDie = false;

        switch (level) {
            case UNFORMATTED:
                break;

            case WARNING:
                messagePrefix = ANSI_BOLD + ANSI_PURPLE + "warning: " + ANSI_WHITE;
                warningCount++;
                break;

            case SYNTAX_ERROR:
                messagePrefix = ANSI_BOLD + ANSI_RED + "error: " + ANSI_WHITE;
                break;

            case ERROR:
                messagePrefix = ANSI_BOLD + ANSI_RED + "error: " + ANSI_WHITE;
                errorCount++;
                break;

            case FATAL_ERROR:
                messagePrefix = ANSI_BOLD + ANSI_RED + "error: " + ANSI_WHITE;
                shouldDie = true;
                break;

            default:
                messagePrefix = ANSI_BOLD + ANSI_YELLOW + "message: " + ANSI_WHITE;
                break;
        }

        stderrLn(messagePrefix + message + ANSI_RESET);

        if (shouldDie) {
            die();
        }
    }
}
