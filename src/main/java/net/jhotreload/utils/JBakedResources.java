package net.jhotreload.utils;

import java.io.InputStream;

public final class JBakedResources
{
    private static final String ROOT_PATH = "META-INF/jhotreload/";

    private JBakedResources() {}

    public static InputStream getVariableFile(Class<?> containerClass)
    {
        var resourcePath = ROOT_PATH + containerClass
                .getName()
                .replace('.', '/') + ".json";

        return getResource(resourcePath);
    }

    public static InputStream getConfigFile()
    { return getResource(ROOT_PATH + "@CONFIGJHotReload.json"); }

    private static InputStream getResource(String resourcePath)
    {
        return Thread
                .currentThread()
                .getContextClassLoader()
                .getResourceAsStream(resourcePath);
    }
}