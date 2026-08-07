package net.jhotreload.components;

import net.jhotreload.components.variables.HotPair;
import net.jhotreload.utils.JPaths;

import java.util.ArrayList;
import java.util.HashSet;

public final class HotManager
{
    private static final HashSet<HotPair> hotVariables = new HashSet<>();
    private static int registeredVariablesCount = 0;
    private static boolean isRegisteredVariablesCountDisabled;

    private HotManager() {}

    public static void registerVariable(String variableName, Class<?> variableClass)
    {
        if (hotVariables.add(new HotPair(JPaths.classToPathString(variableClass), variableName)))
        { registeredVariablesCount++; }
    }

    public static ArrayList<String> getVariableNamesIn(Class<?> containerClass)
    {
        var list = new ArrayList<String>();
        for (var v : hotVariables)
        {
            if (v.path().equals(JPaths.classToPathString(containerClass)))
            { list.add(v.name()); }
        }

        return list;
    }

    public static int getRegisteredVariablesCount()
    {
        return registeredVariablesCount;
    }

    public static void disableRegisteredVariablesCount()
    {
        registeredVariablesCount = -1;
        isRegisteredVariablesCountDisabled = true;
    }

    public static boolean isRegisteredVariablesCountDisabled()
    {
        return isRegisteredVariablesCountDisabled;
    }
}
