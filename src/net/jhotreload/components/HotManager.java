package net.jhotreload.components;

import net.jhotreload.components.exceptions.DuplicateHotVariableException;
import net.jhotreload.components.variables.HotPair;
import net.jhotreload.utils.JPaths;

import java.util.ArrayList;

public final class HotManager
{
    private static final ArrayList<HotPair> hotVariables = new ArrayList<>();
    private static int registeredVariablesCount = 0;
    private static boolean isRegisteredVariablesCountDisabled;

    private HotManager() {}

    public static void registerVariable(String variableName, Class<?> variableClass)
    {
        hotVariables.add(new HotPair(JPaths.classToPathString(variableClass), variableName));

        if (hotVariables.size() > hotVariables.stream().distinct().toList().size())
        {
            throw new DuplicateHotVariableException("Hot Variable \"" + variableName + "\" was defined more" +
                " than once");
        }

        registeredVariablesCount++;
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
