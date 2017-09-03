package com.github.seijuro.writer;

import com.github.seijuro.search.SearchURL;

import java.io.BufferedWriter;
import java.io.File;

public abstract class HTMLFileWriter implements HTMLWriter {
    /**
     * Construct
     */
    public HTMLFileWriter() {
    }

    public abstract String getTargetDir(SearchURL searchURL);
    public abstract String getFilename(SearchURL searchURL);

    @Override
    public boolean write(SearchURL searchURL, String html) {
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
