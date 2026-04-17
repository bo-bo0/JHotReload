package net.jhotreload.components.variables;

@SuppressWarnings("unused")
public sealed interface JVariable<T> permits HotVariable, DisabledHotVariable
{
    T get();
    void set(T value);
}
