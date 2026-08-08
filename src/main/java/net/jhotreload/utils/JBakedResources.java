package net.jhotreload.utils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public final class JBakedResources
{
    private static final String ROOT_PATH = "META-INF/jhotreload/";
    private static final String RUNTIME_ROOT_PATH = "JHotReload/";

    private JBakedResources() {}

    public static InputStream getVariableFile(Class<?> containerClass)
    {
        var resourcePath = ROOT_PATH + containerClass.getName().replace('.', '/') + ".json";
        return getResource(resourcePath);
    }

    public static InputStream getConfigFile()
    { return getResource(ROOT_PATH + "@CONFIGJHotReload.json"); }

    public static InputStream getFile(Path filePath)
    {
        var normalizedPath = filePath
                .normalize()
                .toString()
                .replace('\\', '/');

        int rootIndex = normalizedPath.indexOf(RUNTIME_ROOT_PATH);

        if (rootIndex < 0)
        { return null; }

        var relativePath = normalizedPath.substring(rootIndex + RUNTIME_ROOT_PATH.length());

        return getResource(ROOT_PATH + relativePath);
    }

    public static boolean copyToFileIfPresent(Path targetPath) throws IOException
    {
        try (var inputStream = getFile(targetPath))
        {
            if (inputStream == null)
            { return false; }

            var parent = targetPath.getParent();

            if (parent != null)
            { Files.createDirectories(parent); }

            Files.copy
            (
                inputStream,
                targetPath,
                StandardCopyOption.REPLACE_EXISTING
            );

            return true;
        }
    }

    private static InputStream getResource(String resourcePath)
    {
        return Thread
                .currentThread()
                .getContextClassLoader()
                .getResourceAsStream(resourcePath);
    }
}