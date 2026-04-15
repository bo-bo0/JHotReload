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
                String ph;

                ph = l.substring(0, l.indexOf(':'));
                ph = ph.replace(',', ' ');
                ph = ph.replace(':', ' ');
                ph = ph.trim();

                if (ph.equals("\"" + variableName + "\""))
                {
                    String[] tokens = l.split("\\s+");
                    val = tokens[2];

                    if (valExample instanceof String || valExample instanceof Character)
                    {
                        var builder = new StringBuilder(val);

                        for (int i = 3; i < tokens.length; i++)
                        {
                            builder.append(" ");
                            builder.append(tokens[i]);
                        }

                        if (builder.charAt(0) == '\"')
                        { builder.replace(0, 1, ""); }

                        int len = builder.length();
                        if (builder.charAt(len - 1) == '\"')
                        { builder.replace(len - 1, len, ""); }

                        val = builder.toString();
                    }

                    break;
                }
            }
        }
        return val;
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
