package com.github.seijuro.writer;

import com.github.seijuro.TSVConvertable;

import java.io.*;
import java.util.Objects;

public class TSVFileWriter implements Closeable, AutoCloseable {
    private final String filename;
    private final String targetDir;

    private BufferedWriter writer;

    public TSVFileWriter(String targetDir, String filename) throws IOException {
        this.targetDir = targetDir;
        this.filename = filename;

        File dir = new File(this.targetDir);
        dir.mkdirs();


        StringBuffer pathBuilder = new StringBuffer(this.targetDir);
        pathBuilder.append(File.separator).append(this.filename);

        writer = new BufferedWriter(new FileWriter(pathBuilder.toString(), true));
    }

    public void write(TSVConvertable obj) throws IOException {
        if (Objects.nonNull(writer)) {
            writer.write(obj.toTSV());
            writer.write(System.lineSeparator());
        }
    }

    @Override
    public void close() throws IOException {
        try {
            if (Objects.nonNull(writer)) {
                writer.close();
            }
        }
        finally {
            writer = null;
        }
    }
}