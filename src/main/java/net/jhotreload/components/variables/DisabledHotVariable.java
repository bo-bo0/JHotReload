package net.jhotreload.components.variables;

import net.jhotreload.jsonparser.Caster;
import net.jhotreload.jsonparser.JReader;
import net.jhotreload.utils.JThrowHelper;

import java.io.IOException;
import java.nio.file.Path;

public final class DisabledHotVariable<T> implements JVariable<T>
{
    private T value;

    /*package-private*/ DisabledHotVariable(T value, Path filePath, String variableName)
    {
        var reader = new JReader<>(filePath, variableName, value);

        String readValue = null;

        try
        { readValue = reader.readVariableStringValue(); }
        catch (IOException ex)
        { JThrowHelper.signalJReadFailure("Could not access \"" + filePath + "\" to read the value of \"" + variableName + "\""); }

        if (readValue == null)
        { this.value = value; }
        else
        {
            try
            {
                var caster = new Caster<T>();
                this.value = caster.castString(readValue, value);
            }
            catch (NumberFormatException ex)
            { this.value = value; }
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
