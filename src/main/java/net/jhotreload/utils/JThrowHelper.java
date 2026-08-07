package net.jhotreload.utils;

import net.jhotreload.components.JHotReloadConfig;
import net.jhotreload.jsonparser.exceptions.JReadException;
import net.jhotreload.jsonparser.exceptions.JWriteException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

public final class JThrowHelper
{
    private JThrowHelper() {}

    public static void signalJReadFailure(String message)
    {
        updateErrorLog(message);

        if (JHotReloadConfig.isCrashOnJReadWriteFailureActive())
        { throw new JReadException(message); }
        else
        { System.err.println("JRead failure: " + message); }
    }

    public static void signalJWriteFailure(String message)
    {
        updateErrorLog(message);

        if (JHotReloadConfig.isCrashOnJReadWriteFailureActive())
        { throw new JWriteException(message); }
        else
        { System.err.println("JWrite failure: " + message); }
    }

    private static void updateErrorLog(String message)
    {
        try
        {
            Files.writeString
            (
                JPaths.getJHotReloadErroLogFilePath(),
            message + "\n",
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND
            );
        }
        catch (IOException ex) { System.err.println("JHotReload failed to update error log"); }
    }
}
