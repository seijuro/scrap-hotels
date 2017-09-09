package com.github.seijuro.site.com.tripadvisor;

import com.github.seijuro.site.com.tripadvisor.query.Destination;
import com.github.seijuro.site.com.tripadvisor.query.Sort;
import com.github.seijuro.writer.HTMLWriter;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.io.FileWriter;
import java.util.Objects;

@Log4j2
public class TripAdvisorSearchResultHTMLFileWriter implements HTMLWriter {
    /**
     * Instance Properties
     */
    private final String rootDir;

    /**
     * Construct
     *
     * @param rootDir
     */
    public TripAdvisorSearchResultHTMLFileWriter(String rootDir) {
        Objects.requireNonNull(rootDir);

        this.rootDir = rootDir;
    }

    public String getTargetDirpath(String searchURL, Destination destination, Sort sort, int page) {
        StringBuilder pathBuilder = new StringBuilder(rootDir);

        if (Objects.nonNull(destination)) {
            pathBuilder.append(File.separator).append(destination.getLabel());
        }

        if (Objects.nonNull(sort)) {
            pathBuilder.append(File.separator).append(sort.getLabel());
        }

        return pathBuilder.toString();
    }

    public String getFilename(String searchURL, Destination destination, Sort sort, int page) {
        return String.format("%03d.html", page);
    }

    @Override
    public boolean write(String searchURL, String html, Object... options) {
        Destination destination = null;
        Sort sort = null;
        int page = -1;

        if (options.length > 0 &&
                options[0] instanceof Destination) {
            destination = Destination.class.cast(options[0]);
        }

        if (options.length > 1 &&
                options[1] instanceof Sort) {
            sort = Sort.class.cast(options[1]);
        }

        if (options.length > 2 &&
                options[2] instanceof Integer) {
            page = Integer.class.cast(options[2]).intValue();
        }

        // Log
        log.debug("destination : {}, sort : {}, page : {}", destination.getLabel(), sort.getLabel(), page);
        try {
            File dirpath = new File(getTargetDirpath(searchURL, destination, sort, page));
            String filename = getFilename(searchURL, destination, sort, page);

            if (!dirpath.exists()) {
                dirpath.mkdirs();
            }

            FileWriter fwriter = new FileWriter(String.format("%s%s%s", dirpath.getAbsolutePath(), File.separator, filename), false);

            fwriter.write(html);
            fwriter.close();

            return true;
        }
        catch (Exception excp) {
            excp.printStackTrace();
        }

        return false;
    }
}
