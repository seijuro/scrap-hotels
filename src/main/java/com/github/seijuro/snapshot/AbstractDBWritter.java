package com.github.seijuro.snapshot;

import com.github.seijuro.site.com.hotels.result.Result;
import lombok.Getter;

import java.io.IOException;
import java.sql.Connection;

public abstract class AbstractDBWritter<T> implements RecordWriter<T> {
    @Getter
    private final static int DefaultCommitBlockSize = 500;

    /**
     * Instance Properties
     */
    protected Connection connection;

    public AbstractDBWritter(Connection conn) throws NullPointerException {
        if (conn == null) {
            throw new NullPointerException("Param, conn, is a null object.");
        }

        connection = conn;
    }

    @Override
    public void close() throws IOException {
        try {
            if (connection != null) {
                connection.close();
            }
        }
        catch (Exception excp) {
            excp.printStackTrace();
        }
        finally {
            connection = null;
        }
    }

    public void commit() {
        try {
            if (connection != null) { connection.commit(); }
        }
        catch (Exception excp) {
            excp.printStackTrace();
        }
    }
}
