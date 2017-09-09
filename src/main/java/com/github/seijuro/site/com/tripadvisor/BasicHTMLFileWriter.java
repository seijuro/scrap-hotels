package com.github.seijuro.site.com.tripadvisor;

import com.github.seijuro.site.common.writer.HTMLFileWriter;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class BasicHTMLFileWriter extends HTMLFileWriter {
    /**
     * Instance Properties
     */
    private final String root;

    public BasicHTMLFileWriter(String rootDirectoryPath) {
        Objects.requireNonNull(rootDirectoryPath);

        root = rootDirectoryPath;
    }

    public boolean remove(String[] dirHierarchy) {
        try {
            StringBuilder pathBuilder = new StringBuilder(root);

            for (String subdir : dirHierarchy) {
                pathBuilder.append(File.separator).append(subdir);
            }

            String targetPath = pathBuilder.toString();
            super.remove(targetPath);

            return true;
        }
        catch (IOException excp) {
            excp.printStackTrace();
        }

        return false;
    }

    public boolean notExists(String[] dirHierarchy) {
        return !exists((dirHierarchy));
    }

    public boolean exists(String[] dirHierarchy) {
        StringBuilder pathBuilder = new StringBuilder(root);

        for (String subdir : dirHierarchy) {
            pathBuilder.append(File.separator).append(subdir);
        }

        String targetPath = pathBuilder.toString();
        return super.exists(targetPath);
    }

    public boolean write(String[] dirHierarchy, String filename, String pageSource) {
        StringBuilder pathBuilder = new StringBuilder(root);

        for (String subdir : dirHierarchy) {
            pathBuilder.append(File.separator).append(subdir);
        }

        pathBuilder.append(File.separator).append(filename);
        String targetPath = pathBuilder.toString();


        try {
            super.write(targetPath, pageSource);

            return true;
        }
        catch (Exception excp) {
            excp.printStackTrace();
        }

        return false;
    }
}
