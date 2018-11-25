import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class ExplainVisitor extends XplnBaseVisitor<Object> {
    private Map<String, ExplainFunctionSignature> functionSignatures
        = new HashMap<String, ExplainFunctionSignature>();
    private Boolean currentFunctionHasReturn = false;
    private Boolean programBodyHasReturn = false;
    private Boolean currentlyVisitingAFunction = false;

    private String filename;

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public Boolean getProgramBodyHasReturn() {
        return programBodyHasReturn;
    }

    @Override
    public Object visitFuncdef(XplnParser.FuncdefContext ctx) {
        currentlyVisitingAFunction = true;

        String identifier = ctx.ID().getText();
        String identifierNoCase = identifier.toLowerCase();
        int argumentCount = ctx.arglist().ID().size();
        int currentLine = ctx.getStart().getLine();
        int currentColumn = ctx.getStart().getCharPositionInLine();

        if (functionSignatures.containsKey(identifierNoCase))
        {
            ExplainFunctionSignature signature = functionSignatures.get(identifierNoCase);

            ExplainParserMessage.sendLocation(filename, currentLine, currentColumn);
            ExplainParserMessage.send("function with name " + identifier + " was already defined at line "
                    + signature.getDefinitionLineIndex(), ExplainSeverityLevel.WARNING);
        }

        functionSignatures.put(identifierNoCase,
            new ExplainFunctionSignature(identifier, currentLine, argumentCount));

        // Check the contained statements for a return statement, the return context visitor
        // sets the relevant variable to true. Immediately consume it and bypass the following
        // error handler.
        Object childrenResult = visitChildren(ctx);
        Boolean hasReturn = currentFunctionHasReturn;
        currentFunctionHasReturn = false;

        if (!hasReturn) {
            ExplainParserMessage.sendLocation(filename, currentLine, currentColumn);
            ExplainParserMessage.send("function " + identifier + " is missing a return statement",
                ExplainSeverityLevel.ERROR);
        }

        currentlyVisitingAFunction = false;
        return childrenResult;
    }

    @Override
    public Object visitFunccall(XplnParser.FunccallContext ctx) {
        String identifier = ctx.ID().getText();
        String identifierNoCase = identifier.toLowerCase();
        int currentLine = ctx.getStart().getLine();
        int currentColumn = ctx.getStart().getCharPositionInLine();

        int argumentCount = ctx.arglist_call().expr().size();

        if (!functionSignatures.containsKey(identifierNoCase))
        {
            ExplainParserMessage.sendLocation(filename, currentLine, currentColumn);
            ExplainParserMessage.send("call to undefined function " + identifier,
                ExplainSeverityLevel.ERROR);
        }
        else
        {
            ExplainFunctionSignature signature = functionSignatures.get(identifierNoCase);
            int expectedArgumentCount = signature.getArgumentCount();

            if (expectedArgumentCount > argumentCount) {
                ExplainParserMessage.sendLocation(filename, currentLine, currentColumn);
                ExplainParserMessage.send("too few arguments passed to " + identifier + ", expected "
                        + expectedArgumentCount + ", have " + argumentCount,
                    ExplainSeverityLevel.WARNING);
            }
            else if (expectedArgumentCount < argumentCount) {
                ExplainParserMessage.sendLocation(filename, currentLine, currentColumn);
                ExplainParserMessage.send("too many arguments passed to " + identifier + ", expected "
                        + expectedArgumentCount + ", have " + argumentCount,
                    ExplainSeverityLevel.WARNING);
            }
        }

        return visitChildren(ctx);
    }

    @Override
    public Object visitReturn_stmt(XplnParser.Return_stmtContext ctx) {
        if (!currentlyVisitingAFunction) {
            programBodyHasReturn = true;
        }
        else {
            currentFunctionHasReturn = true;
        }

        return visitChildren(ctx);
    }
}
