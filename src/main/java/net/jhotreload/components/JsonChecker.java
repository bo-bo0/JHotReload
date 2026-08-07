package net.jhotreload.components;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;

/*package-private*/ final class JsonChecker
{
    private JsonChecker() {}

    /*package-private*/ static void deleteUnusedJsonFiles(Path jsonRoot) throws IOException
    {
        try (var paths = Files.walk(jsonRoot))
        {
            var unusedFile = new ArrayList<Path>();

            paths
                .filter(Files::isRegularFile)
                .filter(path -> path.toString().endsWith(".json"))
                .filter(path -> !path.getFileName().toString().startsWith("@"))
                .forEach(jsonFile ->
                {
                    var relativePath = jsonRoot.relativize(jsonFile);

                    var className = relativePath
                            .toString()
                            .substring(0, relativePath.toString().length() - ".json".length())
                            .replace('/', '.')
                            .replace('\\', '.');

                    if (!classExists(className))
                    { unusedFile.add(jsonFile); }
                });

            for (var path : unusedFile)
            {
                if (Files.exists(path))
                { Files.delete(path); }
            }

            deleteEmptyDirectories(jsonRoot);
        }
    }

    private static boolean classExists(String className)
    {
        var resourcePath = className.replace('.', '/') + ".class";

        return JsonChecker.class
                .getClassLoader()
                .getResource(resourcePath) != null;
    }

    private static void deleteEmptyDirectories(Path root) throws IOException
    {
        Files.walkFileTree(root, new SimpleFileVisitor<>()
        {
            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException ex) throws IOException
            {
                if (ex != null)
                { throw ex; }

                if (dir.equals(root))
                { return FileVisitResult.CONTINUE; }

                try { Files.delete(dir); }
                catch (DirectoryNotEmptyException ignored) {}

                return FileVisitResult.CONTINUE;
            }
        });
    }
}