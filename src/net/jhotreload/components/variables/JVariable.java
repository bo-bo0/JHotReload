package net.jhotreload.components.variables;

@SuppressWarnings("unused")
public interface JVariable<T>
{
    T get();
    void set(T value);
}
