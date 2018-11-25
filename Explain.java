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
            ExplainParserMessage.send("no input file was specified",
                ExplainSeverityLevel.FATAL_ERROR);
        }


        if (inputFile != null) {
            try {
                is = new FileInputStream(inputFile);
            }
            catch (FileNotFoundException e) {
                ExplainParserMessage.send("cannot read file " + e.getMessage(),
                    ExplainSeverityLevel.FATAL_ERROR);
            }
        }
        else {
            ExplainParserMessage.send("no input file was specified",
                ExplainSeverityLevel.FATAL_ERROR);
        }

        ANTLRInputStream input = new ANTLRInputStream(is);
        XplnLexer lexer = new XplnLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        XplnParser parser = new XplnParser(tokens);

        ParseTree tree = parser.file_input();
        ExplainVisitor visitor = new ExplainVisitor();

        visitor.visit(tree);

        if (!visitor.getProgramBodyHasReturn()) {
            ExplainParserMessage.send("program body is missing a return statement",
                ExplainSeverityLevel.FATAL_ERROR);
        }

        System.out.println(tree.toStringTree(parser));
    }
}
