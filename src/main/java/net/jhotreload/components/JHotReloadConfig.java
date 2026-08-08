package net.jhotreload.components;

import net.jhotreload.jsonparser.JReader;
import net.jhotreload.utils.JPaths;
import net.jhotreload.utils.exceptions.JHotReloadConfigReadException;
import net.jhotreload.utils.exceptions.JHotReloadConfigWriteException;
import net.jhotreload.utils.JBakedResources;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class JHotReloadConfig
{
    private static final String defaultJsonConfig =
                """
                {
                    "isJHotReloadActive" : true,
                    "crashOnJReadWriteFailure" : false
                }
                """;

    private static boolean JHotReloadingIsActive;
    private static boolean crashOnJReadWriteFailure;

    private JHotReloadConfig() {}

    private static void generateDefaultConfigFile(Path configFilePath)
    {
        try
        {
            var dir = configFilePath.getParent();

            if (!Files.exists(dir))
            { Files.createDirectories(dir); }

            if (!Files.exists(configFilePath))
            { Files.createFile(configFilePath); }

            Files.writeString(JPaths.getJHotReloadConfigFilePath(), defaultJsonConfig);
        }

        catch (IOException ex)
        { throw new JHotReloadConfigWriteException("JHot Reload failed to generate default config file."); }
    }

    private static void readConfigFromFile(Path configFilePah)
    {
        try
        {
            var booleanReader = new JReader<>(configFilePah, "isJHotReloadActive", JHotReloadingIsActive);
            JHotReloadingIsActive = booleanReader.read();

            booleanReader = new JReader<>(configFilePah, "crashOnJReadWriteFailure", crashOnJReadWriteFailure);
            crashOnJReadWriteFailure = booleanReader.read();
        }

        catch (IOException ex)
        { throw new JHotReloadConfigReadException("JHot Reload failed to read config from file " + configFilePah); }
    }

    public static void init()
    {
        var configFilePath = JPaths.getJHotReloadConfigFilePath();

        if (!Files.exists(configFilePath))
        {
            try
            {
                if (!JBakedResources.copyToFileIfPresent(configFilePath))
                { generateDefaultConfigFile(configFilePath); }
            }
            catch (IOException ex)
            { throw new JHotReloadConfigReadException("JHot Reload failed to restore " + "baked config file."); }
        }

        readConfigFromFile(configFilePath);

        if (isJHotReloadingActive())
        {
            if (Files.exists(JPaths.getJHotReloadErroLogFilePath()))
            {
                try
                { Files.delete(JPaths.getJHotReloadErroLogFilePath()); }
                catch (IOException ex)
                { System.err.println("JHotReload failed to delete " + "latest error log file"); }
            }

            try
            { JsonChecker.deleteUnusedJsonFiles(Path.of("JHotReload")); }
            catch (IOException ex)
            {System.err.println("JHotReload failed to delete " + "unused JSON files"); }
        }
    }

    public static boolean isJHotReloadingActive()
    {
        return JHotReloadingIsActive;
    }

    public static boolean isCrashOnJReadWriteFailureActive()
    {
        return crashOnJReadWriteFailure;
    }
}
