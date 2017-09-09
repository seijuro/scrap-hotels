package com.github.seijuro;

import com.github.seijuro.writer.HTMLFileWriter;
import com.github.seijuro.search.query.Destination;
import com.github.seijuro.search.SearchURL;
import com.github.seijuro.search.query.Sort;

import java.io.File;

public class DefaultHTMLFileWriter extends HTMLFileWriter {
    /**
     * Construct
     *
     * @param baseDir
     */
    public DefaultHTMLFileWriter(String baseDir) {
        super(baseDir);
    }

    @Override
    public String getTargetDir(SearchURL searchURL) {
        StringBuffer pathBuilder = new StringBuffer(getRootDir());

        Destination destination = searchURL.getDestination();
        Sort sort = searchURL.getSort();

        pathBuilder.append(File.separator).append(sort.getLabel());
        pathBuilder.append(File.separator).append(destination.getLabel());

        return pathBuilder.toString();
    }

    @Override
    public String getFilename(SearchURL searchURL) {
        return String.format("%03d.html", searchURL.getPage());
    }


}
