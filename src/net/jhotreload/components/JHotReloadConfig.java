package net.jhotreload.components;

public abstract class JHotReloadConfig
{
    private static boolean JHotReloadingIsActive = true;

    public static boolean isJHotReloadingActive()
    {
        return JHotReloadingIsActive;
    }
}
