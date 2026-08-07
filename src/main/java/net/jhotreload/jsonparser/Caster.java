package net.jhotreload.jsonparser;

public class Caster<T>
{
    @SuppressWarnings("unused")
    public T castString(String value, T destinationTypeValueExample)
    {
        var type = getGenericClassOf(destinationTypeValueExample);

        return switch (destinationTypeValueExample)
        {
            case Integer i -> type.cast(Integer.valueOf(value));
            case Float f -> type.cast(Float.valueOf(value));
            case Double d -> type.cast(Double.valueOf(value));
            case Boolean b -> type.cast(Boolean.valueOf(value));
            case Character c -> type.cast(value.charAt(0));

            default -> type.cast(value);
        };
    }

    @SuppressWarnings("unchecked")
    public Class<T> getGenericClassOf(T value)
    {
        return (Class<T>)value.getClass();
    }

    @SuppressWarnings("unchecked")
    public T unsafeGenericCast(Object value)
    {
        return (T)value;
    }
}
