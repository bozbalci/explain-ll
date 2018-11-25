import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class ExplainVisitor extends XplnBaseVisitor<Object> {
    private Map<String, ExplainFunctionSignature> functionSignatures
        = new HashMap<String, ExplainFunctionSignature>();
    private Boolean currentFunctionHasReturn = false;
    private Boolean programBodyHasReturn = false;
    private Boolean currentlyVisitingAFunction = false;

    public Boolean getProgramBodyHasReturn() {
        return programBodyHasReturn;
    }

    @Override
    public Object visitFuncdef(XplnParser.FuncdefContext ctx) {
        currentlyVisitingAFunction = true;

        String identifier = ctx.ID().getText();
        int argumentCount = ctx.arglist().ID().size();
        int currentLine = ctx.getStart().getLine();

        if (functionSignatures.containsKey(identifier))
        {
            ExplainFunctionSignature signature = functionSignatures.get(identifier);

            ExplainParserMessage.send("function with name " + identifier + " was already defined at line "
                    + signature.getDefinitionLineIndex(), ExplainSeverityLevel.WARNING);
        }

        functionSignatures.put(identifier,
            new ExplainFunctionSignature(identifier, currentLine, argumentCount));

        // Check the contained statements for a return statement, the return context visitor
        // sets the relevant variable to true. Immediately consume it and bypass the following
        // error handler.
        visit(ctx.block_stmt());
        Boolean hasReturn = currentFunctionHasReturn;
        currentFunctionHasReturn = false;

        if (!hasReturn) {
            ExplainParserMessage.send("function " + identifier + " is missing a return statement",
                ExplainSeverityLevel.ERROR);
        }

        currentlyVisitingAFunction = false;
        return hasReturn;
    }

    @Override
    public Object visitFunccall(XplnParser.FunccallContext ctx) {
        String identifier = ctx.ID().getText();
        int currentLine = ctx.getStart().getLine();
        int currentColumn = ctx.getStart().getCharPositionInLine();

        int argumentCount = ctx.arglist_call().expr().size();

        if (!functionSignatures.containsKey(identifier))
        {
            ExplainParserMessage.send("call to undefined function " + identifier
                + " at line " + currentLine + ", column " + currentColumn, ExplainSeverityLevel.ERROR);
        }
        else
        {
            ExplainFunctionSignature signature = functionSignatures.get(identifier);
            int expectedArgumentCount = signature.getArgumentCount();

            if (expectedArgumentCount > argumentCount) {
                ExplainParserMessage.send("too few arguments to function call, expected "
                    + expectedArgumentCount + ", have " + argumentCount
                    + " at line " + currentLine + ", column " + currentColumn, ExplainSeverityLevel.ERROR);
            }
            else if (expectedArgumentCount < argumentCount) {
                ExplainParserMessage.send("too many arguments to function call, expected "
                    + expectedArgumentCount + ", have " + argumentCount
                    + " at line " + currentLine + ", column " + currentColumn, ExplainSeverityLevel.ERROR);
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
