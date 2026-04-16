package net.jhotreload.components.variables;

import net.jhotreload.components.HotManager;
import net.jhotreload.components.JHotReloadConfig;
import net.jhotreload.components.exceptions.HotVariableContainerClassNotFoundException;
import net.jhotreload.components.exceptions.InvalidHotVariableTypeException;
import net.jhotreload.jsonparser.JReader;
import net.jhotreload.jsonparser.JWriter;
import net.jhotreload.jsonparser.exceptions.JReadException;
import net.jhotreload.jsonparser.exceptions.JWriteException;
import net.jhotreload.utils.JPaths;

import java.io.IOException;
import java.nio.file.Path;

public final class HotVariable<T> implements JVariable<T>
{
    private final JReader<T> reader;
    private final String name;
    private final String filePathString;

    private T lastValidValue;
    private T value;

    private void validTypeCheck(T value)
    {
        boolean valid =
                value instanceof Integer ||
                value instanceof Float ||
                value instanceof Double ||
                value instanceof String ||
                value instanceof Character;

        if (!valid)
        { throw new InvalidHotVariableTypeException(value.getClass() + " cannot be used as the type of a Hot Variable"); }
    }

    /**
     * Instantiates a Hot Variable of the specified value and name and returns it.
     *
     * <p>
     *
     * The instantiated variable will be saved in a json file with the same name of the class where the Hot Variable
     * has been declared. You'll find said file at: root\JHotReload\package\className.json
     *
     * @param value The value of the Hot Variable, its type will be automatically detected,
     *              can only be a wrapper of a primitive type or a String.
     *
     * @param name The name that will be assigned to the variable in the .json file, Hot Variables contained in the same class
     *             cannot share the same name (not even if they are declared in different scopes).
     *
     * @author bo bo
     * @since 2026-04-15
     */
    public static <T> JVariable<T> of(T value, String name)
    {
        Class<?> containerClass = StackWalker
                .getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE)
                .getCallerClass();

        if (JHotReloadConfig.isJHotReloadingActive())
        { return new HotVariable<>(value, name, containerClass); }
        else
        { return new DisabledHotVariable<>(value); }
    }

    private HotVariable(T value, String name, Class<?> containerClass)
    {
        validTypeCheck(value);

        if (containerClass.getName().equals(this.getClass().getName()))
        { throw new HotVariableContainerClassNotFoundException("Cannot locate container class of Hot Variable \"" + name + "\""); }

        this.value = value;
        this.lastValidValue = value;
        this.name = name;
        this.filePathString = String.valueOf(JPaths.classToFullJsonPath(containerClass));
        reader = new JReader<>(Path.of(filePathString), name, value);
        HotManager.registerVariable(name, containerClass);
        writeInFile(containerClass);
    }

    /**
     * Returns the value of the Hot Variable.
     *
     * @return The value assigned to the variable in the json file, if said value is invalid or absent the last
     * stored valid value will be returned instead.
     *
     * @author bo bo
     * @since 2026-04-15
     */
    @Override
    public T get()
    {
        try
        {
            value = reader.read();
            lastValidValue = value;
            return value;
        }

        catch (IOException ex)
        { throw new JReadException("Hot Variable \"" + name + "\" failed to read from " + filePathString); }

        catch (NumberFormatException | StringIndexOutOfBoundsException | ArrayIndexOutOfBoundsException ex)
        { return lastValidValue; }
    }

    @Override
    public void set(T value)
    {
        this.value = value;
    }

    private String getJsonValue()
    {
        String val;

        if (value instanceof String || value instanceof Character)
        { val = "\"" + value + "\"";}
        else
        { val = value.toString(); }

        return "\"" + name + "\" : " + val;
    }

    private void writeInFile(Class<?> containerClass)
    {
        Path path = JPaths.classToFullJsonPath(containerClass);
        var writer = new JWriter(path);

        try
        { writer.write(getJsonValue(), containerClass); }
        catch (IOException ex)
        { throw new JWriteException("Hot Variable \"" + name + "\" failed to be written in " + path); }
    }

    @Override
    public String toString()
    {
        return "Hot Variable -> " + getJsonValue();
    }
}
