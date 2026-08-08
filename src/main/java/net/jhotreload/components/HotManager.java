package net.jhotreload.components;

import net.jhotreload.components.variables.HotPair;
import net.jhotreload.components.variables.HotVariable;
import net.jhotreload.utils.JPaths;

import java.util.ArrayList;
import java.util.HashMap;

public final class HotManager
{
    private static final HashMap<HotPair, HotVariable<?>> hotVariables = new HashMap<>();
    private static int registeredVariablesCount = 0;
    private static boolean isRegisteredVariablesCountDisabled;

    private HotManager() {}

    public static HotVariable<?> registerVariable(String variableName, Class<?> variableClass, HotVariable<?> hotVariable)
    {
        var key = new HotPair(JPaths.classToPathString(variableClass), variableName);

        if (hotVariables.containsKey(key))
        { return hotVariables.get(key); }

        else
        {
            hotVariables.put(key, hotVariable);
            registeredVariablesCount++;
            return null;
        }
    }

    public static ArrayList<String> getVariableNamesIn(Class<?> containerClass)
    {
        var list = new ArrayList<String>();
        for (var v : hotVariables.keySet())
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
