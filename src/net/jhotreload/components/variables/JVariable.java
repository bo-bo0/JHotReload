package net.jhotreload.components.variables;

public interface JVariable<T>
{
    T get();
    void set(T value);
}
