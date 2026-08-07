package net.jhotreload.jsonparser;

public class ParserUtils
{
    /*package-private*/ static String removePunctuationFromString(String string, char punctuation)
    {
        var fixerBuilder = new StringBuilder(string);
        int fixerIndex;

        do
        {
            fixerIndex = fixerBuilder.indexOf("" + punctuation);

            if (fixerIndex > -1)
            { fixerBuilder.replace(fixerIndex, fixerIndex + 1, ""); }

        } while(fixerIndex > -1);

        return fixerBuilder.toString();
    }

    /*package-private*/ static String removePunctuationFromString(String string, char... punctuation)
    {
        String result = string;

        for (var p : punctuation)
        { result = removePunctuationFromString(result, p); }

        return result;
    }

    /*package-private*/ static StringBuilder trimStringBuilder(StringBuilder stringBuilder)
    {
        return new StringBuilder(stringBuilder.toString().trim());
    }
}
