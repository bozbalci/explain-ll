public class ExplainParserMessage {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    public static void send(String message, ExplainSeverityLevel level) {
        String messagePrefix = null;
        Boolean shouldDie = false;

        switch (level) {
            case WARNING:
                messagePrefix = ANSI_CYAN + "warning: " + ANSI_WHITE;
                break;
            case ERROR:
                messagePrefix = ANSI_RED + "error: " + ANSI_WHITE;
                break;
            case FATAL_ERROR:
                messagePrefix = ANSI_RED + "fatal error: " + ANSI_WHITE;
                shouldDie = true;
                break;
        }

        System.err.println(messagePrefix + message + ANSI_RESET);

        if (shouldDie) {
            System.exit(1);
        }
    }
}
