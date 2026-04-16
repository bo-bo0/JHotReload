package net.jhotreload.components;

public abstract class JHotReloadConfig
{
    private static boolean JHotReloadingIsActive = false;

    public static boolean isJHotReloadingActive()
    {
        return JHotReloadingIsActive;
    }
}
