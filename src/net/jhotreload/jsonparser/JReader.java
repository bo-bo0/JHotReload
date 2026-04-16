package net.jhotreload.jsonparser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class JReader<T>
{
    private final Path filePath;
    private final String variableName;
    private final T valExample;

    private Class<T> type;

    public JReader(Path filePath, String variableName, T valExample)
    {
        this.filePath = filePath;
        this.variableName = variableName;
        this.valExample = valExample;

        setType(valExample);
    }

    @SuppressWarnings("unchecked")
    private void setType(T valExample)
    {
        type = (Class<T>)valExample.getClass();
    }

    private String findValueInJson(ArrayList<String> fileLines)
    {
        String val = "";

        for (var l : fileLines)
        {
            if (l.contains(":"))
            {
                String readVariableName = l.substring(0, l.indexOf(':'))
                        .replace(',', ' ')
                        .replace(':', ' ')
                        .trim();

                if (readVariableName.equals("\"" + variableName + "\""))
                {
                    int lastIndex = (l.contains(",") ? l.lastIndexOf(",") : l.length());
                    val = l.substring(l.indexOf(':') + 1, lastIndex);

                    if (valExample instanceof String || valExample instanceof Character)
                    {
                        String[] tokens = l.split(":");
                        val = formatString(val, tokens);
                    }

                    else
                    { val = val.trim(); }

                    break;
                }
            }
        }
        return val;
    }

    private static String formatString(String val, String[] tokens)
    {
        var builder = new StringBuilder(val);

        for (int i = 3; i < tokens.length; i++)
        {
            builder.append(" ");
            builder.append(tokens[i]);
        }

        if (builder.charAt(0) == ' ')
        { builder.replace(0, 1, ""); }

        int indexOfFirstQuotationMarks = builder.indexOf("\"");

        if (indexOfFirstQuotationMarks >= 0)
        { builder.replace(0, indexOfFirstQuotationMarks + 1, ""); }

        int indexOfLastQuotationMarks = builder.indexOf("\"");
        if (indexOfLastQuotationMarks >= 0)
        { builder.replace(indexOfLastQuotationMarks, indexOfLastQuotationMarks + 1, ""); }

        return builder.toString();
    }

    public T read() throws IOException
    {
        var fileLines = new ArrayList<>(Files.readAllLines(filePath));
        String val = findValueInJson(fileLines);

        return switch (valExample)
        {
            case Integer _ -> type.cast(Integer.valueOf(val));
            case Float _ -> type.cast(Float.valueOf(val));
            case Double _ -> type.cast(Double.valueOf(val));
            case Boolean _ -> type.cast(Boolean.valueOf(val));
            case Character _ -> type.cast(val.charAt(0));

            default -> type.cast(val);
        };
    }
}
