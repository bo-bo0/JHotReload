package net.jhotreload.components;

import net.jhotreload.jsonparser.JReader;
import net.jhotreload.utils.JPaths;
import net.jhotreload.utils.exceptions.JHotReloadConfigReadException;
import net.jhotreload.utils.exceptions.JHotReloadConfigWriteException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class JHotReloadConfig
{
    private static final String defaultJsonConfig =
                """
                {
                    "isJHotReloadActive" : true
                }
                """;

    private static boolean JHotReloadingIsActive;

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
        }

        catch (IOException ex)
        { throw new JHotReloadConfigReadException("JHot Reload failed to read config from file " + configFilePah); }
    }

    public static void init()
    {
        var configFilePath = JPaths.getJHotReloadConfigFilePath();

        if (!Files.exists(configFilePath))
        { generateDefaultConfigFile(configFilePath); }

        readConfigFromFile(configFilePath);
    }

    public static boolean isJHotReloadingActive()
    {
        return JHotReloadingIsActive;
    }
}
