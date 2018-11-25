import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import java.io.FileInputStream;
import java.io.InputStream;

public class Explain {
    public static void main(String[] args) throws Exception {
        String inputFile = null;

        if (args.length > 0) {
            inputFile = args[0];
        }

        InputStream is = System.in;

        if (inputFile != null) {
            is = new FileInputStream(inputFile);
        }

        ANTLRInputStream input = new ANTLRInputStream(is);
        XplnLexer lexer = new XplnLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        XplnParser parser = new XplnParser(tokens);

        ParseTree tree = parser.file_input();
        System.out.println(tree.toStringTree(parser));
    }
}
