package net.jhotreload.components;

import net.jhotreload.components.exceptions.DuplicateHotVariableException;
import net.jhotreload.components.variables.HotPair;
import net.jhotreload.utils.JPaths;

import java.util.ArrayList;

public abstract class HotManager
{
    private static final ArrayList<HotPair> hotVariables = new ArrayList<>();

    public static void registerVariable(String variableName, Class<?> variableClass)
    {
        hotVariables.add(new HotPair(JPaths.classToPathString(variableClass), variableName));

        if (hotVariables.size() > hotVariables.stream().distinct().toList().size())
        {
            throw new DuplicateHotVariableException("Hot Variable \"" + variableName + "\" was defined more" +
                " than once");
        }
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
}
