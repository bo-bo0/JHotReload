package net.jhotreload.utils;

public abstract class Paths
{
    public static String classToPath(Class<?> someClass)
    {
        return someClass.getName().replace('.', '/');
    }
}
