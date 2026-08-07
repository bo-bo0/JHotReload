package net.jhotreload.utils;

public class VariableNameValidator
{
    private static final String JAVA_VARIABLE_REGEX =
            "^(?!(?:abstract|assert|boolean|break|byte|case|catch|char|class|const|continue|default|do|" +
                    "double|else|enum|extends|final|finally|float|for|goto|if|implements|import|instanceof" +
                    "|int|interface|long|native|new|package|private|protected|public|return|short|static|" +
                    "strictfp|super|switch|synchronized|this|throw|throws|transient|try|void|volatile|" +
                    "while|_|true|false|null)$)\\p{javaJavaIdentifierStart}\\p{javaJavaIdentifierPart}*$";

    public static void validateVariableName(String name)
    {
        if (!name.matches(JAVA_VARIABLE_REGEX))
        { throw new IllegalArgumentException("identifier \"" + name + "\" is not a valid variable name"); }
    }
}
