package com.github.seijuro.snapshot;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.io.Closeable;
import java.io.IOException;
import java.sql.Connection;

public abstract class AbstractSnapshotDBReader implements AbstractSnapshotReader, Closeable, AutoCloseable {
    /**
     * Instance Properties
     */
    @Getter(AccessLevel.PROTECTED)
    @Setter(AccessLevel.PROTECTED)
    private Connection connection = null;

    /**
     *
     * @param conn
     */
    public AbstractSnapshotDBReader(Connection conn) {
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
}
