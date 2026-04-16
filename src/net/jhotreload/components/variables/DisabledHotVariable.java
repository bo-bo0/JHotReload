package net.jhotreload.components.variables;

public final class DisabledHotVariable<T> implements JVariable<T>
{
    private T value;

    /*package-private*/ DisabledHotVariable(T value)
    {
        this.value = value;
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
