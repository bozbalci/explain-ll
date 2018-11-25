import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.FileNotFoundException;

public class Explain {
    public static void main(String[] args) throws Exception {
        String inputFile = null;
        InputStream is = null;

        if (args.length > 0) {
            inputFile = args[0];
        }
        else {
            ExplainParserMessage.sendUnqualifiedErrorPrefix();
            ExplainParserMessage.send("no input file was specified",
                ExplainSeverityLevel.FATAL_ERROR);
        }


        if (inputFile != null) {
            try {
                is = new FileInputStream(inputFile);
            }
            catch (FileNotFoundException e) {
                ExplainParserMessage.sendUnqualifiedErrorPrefix();
                ExplainParserMessage.send("cannot read file " + e.getMessage(),
                    ExplainSeverityLevel.FATAL_ERROR);
            }
        }
        else {
            ExplainParserMessage.sendUnqualifiedErrorPrefix();
            ExplainParserMessage.send("no input file was specified",
                ExplainSeverityLevel.FATAL_ERROR);
        }

        ANTLRInputStream input = new ANTLRInputStream(is);
        XplnLexer lexer = new XplnLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        XplnParser parser = new XplnParser(tokens);

        // Install our custom error listener
        parser.removeErrorListeners();
        ExplainUnderlineListener underlineListener = new ExplainUnderlineListener();
        underlineListener.setFilename(inputFile);
        parser.addErrorListener(underlineListener);

        // Parse!
        ParseTree tree = parser.file_input();

        int syntaxErrors = parser.getNumberOfSyntaxErrors();

        // Fail early if syntax errors are detected during parsing.
        if (syntaxErrors > 0) {
            String pluralized = (syntaxErrors == 1) ? "error" : "errors";
            ExplainParserMessage.send(syntaxErrors + " " + pluralized + " generated.",
                ExplainSeverityLevel.UNFORMATTED);
            ExplainParserMessage.die();
        }

        ExplainVisitor visitor = new ExplainVisitor();
        visitor.setFilename(inputFile);
        visitor.visit(tree);

        // Perform post-semantic checks
        if (!visitor.getProgramBodyHasReturn()) {
            ExplainParserMessage.sendLocation(inputFile);
            ExplainParserMessage.send("program body is missing a return statement",
                ExplainSeverityLevel.ERROR);
        }

        int warningCount = ExplainParserMessage.warningCount;
        int errorCount = ExplainParserMessage.errorCount;

        String epilogueMessage = "";
        Boolean shouldPrintEpilogue = false;

        if (warningCount > 0) {
            String pluralized = (warningCount == 1) ? "warning" : "warnings";

            epilogueMessage += warningCount + " " + pluralized;

            if (errorCount > 0) {
                epilogueMessage += " and ";
            }

            shouldPrintEpilogue = true;
        }
        if (errorCount > 0) {
            String pluralized = (errorCount == 1) ? "error" : "errors";

            epilogueMessage += errorCount + " " + pluralized;

            shouldPrintEpilogue = true;
        }

        if (shouldPrintEpilogue) {
            epilogueMessage += " generated.";
            ExplainParserMessage.send(epilogueMessage, ExplainSeverityLevel.UNFORMATTED);
        }

        System.out.println(tree.toStringTree(parser));
    }
}
