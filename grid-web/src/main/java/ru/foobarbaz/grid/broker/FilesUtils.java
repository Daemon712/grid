package ru.foobarbaz.grid.broker;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.concurrent.CompletableFuture;

public class FilesUtils {
    public static void cleanDirectoryAsync(Path dir) {
        CompletableFuture.runAsync(()->{
            try {
                Files.walk(dir).sorted(Comparator.reverseOrder()).forEach((path) -> {
                    System.out.println("Delete " + path);
                    try { Files.delete(path); }
                    catch (IOException e) { throw new RuntimeException(e); }
                });
            } catch (IOException e){
                throw new RuntimeException(e);
            }
        });
    }
}
