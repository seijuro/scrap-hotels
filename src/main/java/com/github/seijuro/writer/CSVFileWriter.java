package com.github.seijuro.writer;

import com.github.seijuro.CSVConvertable;
import lombok.Setter;

import java.io.*;
import java.util.Objects;

public class CSVFileWriter implements Closeable, AutoCloseable {
    private final String filename;
    private final String targetDir;

    private BufferedWriter writer;
    @Setter
    private final String columnSeperator = ",";
    @Setter
    private final String recordSeperator = System.lineSeparator();

    public CSVFileWriter(String targetDir, String filename) throws IOException {
        this.targetDir = targetDir;
        this.filename = filename;

        File dir = new File(this.targetDir);
        dir.mkdirs();


        StringBuffer pathBuilder = new StringBuffer(this.targetDir);
        pathBuilder.append(File.separator).append(this.filename);

        writer = new BufferedWriter(new FileWriter(pathBuilder.toString(), true));
    }

    public void write(CSVConvertable obj) throws IOException {
        if (Objects.nonNull(writer)) {
            writer.write(obj.toCSV());
            writer.write(obj.recordSeperator());
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
