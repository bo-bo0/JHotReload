package net.jhotreload.utils;

import java.nio.file.Path;

public final class JPaths
{
    private JPaths() {}

    public static String classToPathString(Class<?> someClass)
    {
       return someClass.getName().replace('.', '/');
    }

    public static Path classToFullJsonPath(Class<?> someClass)
    {
        return Path.of("JHotReload/" + classToPathString(someClass) + ".json");
    }

    public static Path getJHotReloadConfigFilePath()
    {
        return Path.of("JHotReload/@CONFIGJHotReload.json");
    }

    public static Path getJHotReloadErroLogFilePath()
    {
        return Path.of("JHotReload/@ErrorLog.log");
    }
}
