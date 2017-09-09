package com.github.seijuro.writer;

import com.github.seijuro.search.SearchURL;
import lombok.Getter;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.security.MessageDigest;
import java.util.Objects;

public abstract class HTMLFileWriter implements HTMLWriter {
    @Getter
    private final String rootDir;

    /**
     * Construct
     */
    public HTMLFileWriter(String dirpath) {
        rootDir = dirpath;
    }

    public abstract String getTargetDir(SearchURL searchURL);
    public abstract String getFilename(SearchURL searchURL);

    @Override
    public boolean write(String searchURL, String html, Object... objects) {
        String filename;

        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(searchURL.getBytes());

            filename = String.format("%s.html", new String(messageDigest.digest()));
        }
        catch (Exception excp) {
            excp.printStackTrace();

            filename = String.format("%s.html", searchURL.hashCode());
        }

        File directory = new File(rootDir);

        if (!directory.exists()) {
            directory.mkdirs();
        }

        StringBuffer filepathBuilder = new StringBuffer(rootDir);
        filepathBuilder.append(File.separator).append(filename);

        try {
            BufferedWriter writer = new BufferedWriter(new java.io.FileWriter(filepathBuilder.toString()));
            writer.write(html);

            writer.close();

            return true;
        }
        catch (Exception excp) {
            excp.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean write(SearchURL searchURL, String html, Object... objects) {
        String targetDirPath = getTargetDir(searchURL);
        File directory = new File(targetDirPath);

        if (!directory.exists()) {
            directory.mkdirs();
        }

        StringBuffer filepathBuilder = new StringBuffer(targetDirPath);
        filepathBuilder.append(File.separator).append(getFilename(searchURL));

        try {
            BufferedWriter writer = new BufferedWriter(new java.io.FileWriter(filepathBuilder.toString()));
            writer.write(html);

            writer.close();

            return true;
        }
        catch (Exception excp) {
            excp.printStackTrace();
        }

        return false;
    }
}
