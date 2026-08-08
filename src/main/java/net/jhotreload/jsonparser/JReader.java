package net.jhotreload.jsonparser;

import net.jhotreload.utils.JBakedResources;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class JReader<T>
{
    private final Path filePath;
    private final String variableName;
    private final T valExample;
    private final Caster<T> CASTER = new Caster<>();

    public JReader(Path filePath, String variableName, T valExample)
    {
        this.filePath = filePath;
        this.variableName = variableName;
        this.valExample = valExample;
    }

    public T read() throws IOException
    {
        var fileLines = new ArrayList<>(Files.readAllLines(filePath));
        String val = findValueInJson(fileLines);

        return CASTER.castString(val, valExample);
    }

    public String readVariableStringValue() throws IOException
    {
        if (Files.exists(filePath))
        {
            var value = findVariableStringValue(new ArrayList<>(Files.readAllLines(filePath)));

            if (value != null)
            { return value; }
        }

        try (var inputStream = JBakedResources.getFile(filePath))
        {
            if (inputStream == null)
            { return null; }

            try (var reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8)))
            { return findVariableStringValue(new ArrayList<>(reader.lines().toList())); }
        }
    }

    private String findVariableStringValue(ArrayList<String> fileLines)
    {
        for (var line : fileLines)
        {
            var splitLine = line.trim().split("\\s+");

            if (splitLine.length == 0)
            { continue; }

            var currentLineVariableName = getCurrentLineVariableName(splitLine);

            currentLineVariableName = ParserUtils.removePunctuationFromString(currentLineVariableName, '"');

            if (!currentLineVariableName.equals(variableName))
            { continue; }

            var valueBuilder = new StringBuilder(line);

            valueBuilder.replace(0, valueBuilder.indexOf(":") + 1, "");

            valueBuilder = ParserUtils.trimStringBuilder(valueBuilder);

            int commaIndex = valueBuilder.lastIndexOf(",");

            if (commaIndex == valueBuilder.length() - 1)
            {
                valueBuilder.replace(commaIndex, commaIndex + 1, "");

                valueBuilder = ParserUtils.trimStringBuilder(valueBuilder);
            }

            if (valueBuilder.charAt(0) == '"')
            { valueBuilder.replace(0, 1, ""); }

            if (valueBuilder.charAt(valueBuilder.length() - 1) == '"')
            { valueBuilder.replace(valueBuilder.length() - 1, valueBuilder.length(), ""); }

            return valueBuilder.toString().trim();
        }

        return null;
    }

    private static String getCurrentLineVariableName(String[] splitLine)
    {
        var formattedLineBuilder = new StringBuilder(splitLine[0].trim());

        int invalidCharacterIndex = formattedLineBuilder.indexOf(":");

        if (invalidCharacterIndex > -1)
        { formattedLineBuilder.replace(invalidCharacterIndex, invalidCharacterIndex + 1, ""); }

        return formattedLineBuilder.toString();
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
                    int lastIndex = l.length();
                    val = l.substring(l.indexOf(':') + 1, lastIndex).trim();

                    if (val.charAt(val.length() - 1) == ',')
                    { val = val.substring(0, val.length() - 1).trim(); }

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
}
