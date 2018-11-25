// Adapted from The Definitive ANTLR4 Reference, page 156

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

public class ExplainUnderlineListener extends BaseErrorListener {
    private String filename;

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public void syntaxError(Recognizer<?, ?> recognizer,
                            Object offendingSymbol,
                            int line,
                            int charPositionInLine,
                            String message,
                            RecognitionException e) {
        ExplainParserMessage.sendLocation(filename, line, charPositionInLine + 1);
        ExplainParserMessage.send(message, ExplainSeverityLevel.SYNTAX_ERROR);

        underlineError(recognizer, (Token) offendingSymbol, line, charPositionInLine);
    }

    protected void underlineError(Recognizer recognizer,
                                  Token offendingToken,
                                  int line,
                                  int charPositionInLine) {
        CommonTokenStream tokens = (CommonTokenStream) recognizer.getInputStream();
        String input = tokens.getTokenSource().getInputStream().toString();
        String[] lines = input.split("\n");
        String errorLine = lines[line - 1];
        System.err.println(errorLine);

        for (int i = 0; i < charPositionInLine; i++)
            System.err.print(" ");

        int start = offendingToken.getStartIndex();
        int stop = offendingToken.getStartIndex();

        // Prefix the underline (^^^^) with bold and green modifiers
        System.err.print(ExplainParserMessage.ANSI_BOLD + ExplainParserMessage.ANSI_GREEN);

        if (start > 0 && stop >= 0) {
            for (int i = start; i <= stop; i++) {
                System.err.print("^");
            }
        }

        System.err.print(ExplainParserMessage.ANSI_RESET);
        System.err.println();
    }
}
