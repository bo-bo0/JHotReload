package net.jhotreload.jsonparser;

import net.jhotreload.components.HotManager;
import net.jhotreload.jsonparser.exceptions.JWriteException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;


public class JWriter
{
    private final Path filePath;

    public JWriter(Path filePath)
    {
        this.filePath = filePath;
    }

    public void write(String content, Class<?> containerClass) throws IOException
    {
        boolean isNewFile = false;

        if (!Files.exists(filePath.getParent()))
        { Files.createDirectories(filePath.getParent()); }

        if (!Files.exists(filePath))
        {
            Files.createFile(filePath);
            isNewFile = true;
        }

        else
        {
            if (HotManager.getVariableNamesIn(containerClass).size() == 1)
            {
                Files.delete(filePath);
                isNewFile = true;
            }
        }

        rebuildFileWithNewContent(content, isNewFile);
    }

    private void rebuildFileWithNewContent(String content, boolean isNewFile) throws IOException
    {
        if (isNewFile)
        {
            Files.writeString(filePath, "{\n\n\n\n}");
            rebuildFileWithNewContent(content, false);
        }

        else
        {
            var fileLines = new ArrayList<>(Files.readAllLines(filePath));

            for (int i = 1; i < fileLines.size() - 1; i++)
            {
                if (fileLines.get(i - 1).isBlank() && fileLines.get(i + 1).isBlank() && fileLines.get(i).isBlank())
                { fileLines.set(i, content); }
            }

            var newContent = getNewContentStringBuilder(fileLines);
            Files.writeString(filePath, newContent);
        }
    }

    private static StringBuilder getNewContentStringBuilder(ArrayList<String> fileLines)
    {
        var newContent = new StringBuilder();

        int i = 0;
        for (var l : fileLines)
        {
            newContent.append(l);

            if (i < fileLines.size() - 3 && i > 0 && !l.isBlank())
            {
                if (!newContent.substring(newContent.length() - 1).equals(","))
                { newContent.append(" ,"); }
            }

            if (i == fileLines.size() - 2)
            { newContent.append("\n\n"); }

            if (i < fileLines.size() - 1)
            { newContent.append("\n"); }

            i++;
        }

        return newContent;
    }

    public void replaceValue(String variableName, String newValue) throws IOException
    {
        if (!Files.exists(filePath))
        { throw new JWriteException("File: \"" + filePath + "\" could not be located."); }

        var fileLines = new ArrayList<>(Files.readAllLines(filePath));

        int variableLineIndex = 0;
        String variableLineContent = null;

        boolean variableFound = false;
        for (var line : fileLines)
        {
            variableLineContent = line.trim();
            var currentVariableName = variableLineContent.split("\\s+")[0].trim();

            currentVariableName =
                    ParserUtils.removePunctuationFromString(currentVariableName, ',', ':', '}', ']', '{', '[', '"');

            if (currentVariableName.equals(variableName))
            {
                variableFound = true;
                break;
            }

            variableLineIndex++;
        }

        if (!variableFound)
        {
            throw new JWriteException("Failed to replace the value of \"" + variableName + "\"" +
                " because there is no corresponding Hot Variable in \"" + filePath +"\".");
        }

        var newLineBuilder = new StringBuilder(variableLineContent);

        int valueSubstringStart = newLineBuilder.indexOf(":") + 1;
        int valueSubstringEnd = newLineBuilder.length();

        var valueSubstring = newLineBuilder.substring(valueSubstringStart, valueSubstringEnd);

        if (valueSubstring.contains("\""))
        { newValue = "\"" + newValue + "\""; }

        newLineBuilder
                .replace(valueSubstringStart, valueSubstringEnd, "")
                .append(" ")
                .append(newValue);

        if (variableLineContent.contains(","))
        { newLineBuilder.append(" ,"); }

        fileLines.set(variableLineIndex, newLineBuilder.toString());
        Files.write(filePath, fileLines);
    }

}
