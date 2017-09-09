package com.github.seijuro.site.common.writer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class HTMLFileWriter {
    public void write(String targetPath, String pageSource) throws IOException {
        File target = new File(targetPath);
        File parent = target.getParentFile();

        if (!parent.exists()) {
            parent.mkdirs();
        }

        if (parent.isDirectory() &&
                parent.canWrite()) {
            FileWriter fwriter = new FileWriter(target, false);

            fwriter.write(pageSource);

            fwriter.flush();
            fwriter.close();
        }
    }

    public boolean exists(String targetPath) {
        File target = new File(targetPath);

        return target.exists();
    }

    public void remove(String targetPath) throws IOException {
        Path directory = Paths.get(targetPath);
        Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }
}
