package net.jhotreload.components.variables;

import net.jhotreload.jsonparser.Caster;
import net.jhotreload.jsonparser.JReader;
import net.jhotreload.jsonparser.exceptions.JReadException;

import java.io.IOException;
import java.nio.file.Path;

public final class DisabledHotVariable<T> implements JVariable<T>
{
    private T value;

    /*package-private*/ DisabledHotVariable(T value, Path filePath, String variableName)
    {
        var reader = new JReader<>(filePath, variableName, value);

        String readValue;

        try
        { readValue = reader.readVariableStringValue(variableName); }
        catch (IOException ex)
        { throw new JReadException("Could not access \"" + filePath + "\" to read the value of \"" + variableName + "\"."); }

        if (readValue == null)
        { this.value = value; }
        else
        {
            var caster = new Caster<T>();
            this.value = caster.castString(readValue, value);
        }

    }

    @Override
    public T get()
    {
        return value;
    }

    @Override
    public void set(T value)
    {
        this.value = value;
    }

    @Override
    public String toString()
    {
        return "HotVariable (disabled)";
    }
}
