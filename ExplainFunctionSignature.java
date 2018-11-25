public class ExplainFunctionSignature {
    String identifier;
    int definitionLineIndex;
    int argumentCount;

    public ExplainFunctionSignature(String identifier, int definitionLineIndex, int argumentCount) {
        this.identifier = identifier;
        this.definitionLineIndex = definitionLineIndex;
        this.argumentCount = argumentCount;
    }

    public String getIdentifier() {
        return this.identifier;
    }

    public int getDefinitionLineIndex() {
        return this.definitionLineIndex;
    }

    public int getArgumentCount() {
        return this.argumentCount;
    }
}
