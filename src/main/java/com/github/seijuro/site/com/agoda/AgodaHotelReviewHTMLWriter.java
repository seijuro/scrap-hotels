package com.github.seijuro.site.com.agoda;

import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.io.FileWriter;

@Log4j2
public class AgodaHotelReviewHTMLWriter {
    private String targetPath;

    public AgodaHotelReviewHTMLWriter(String targetPath) {
        this.targetPath = targetPath;
    }

    public boolean error(String hotelId, String requestURL) {
        try {
            String errFilepath = String.format("%s%serrors.txt", targetPath, File.separator);
            FileWriter fwriter = new FileWriter(errFilepath, true);

            fwriter.write(String.format("%s:%s\n", hotelId, requestURL));
            fwriter.close();

            return true;
        }
        catch (Exception excp) {
            excp.printStackTrace();
        }

        return false;
    }

    public boolean alreadyVisited(String hotelId) {
        StringBuffer pathBuilder = new StringBuffer(targetPath);
        pathBuilder.append(File.separator).append(hotelId);

        File pathFile = new File(pathBuilder.toString());
        return pathFile.exists();
    }

    public boolean write(String hotelId, int page, String html) {
        try {
            StringBuffer pathBuilder = new StringBuffer(targetPath);
            pathBuilder.append(File.separator).append(hotelId);

            File pathFile = new File(pathBuilder.toString());
            if (!pathFile.exists()) {
                pathFile.mkdirs();
            }

            pathBuilder.append(File.separator).append(String.format("%03d.html", page));
            FileWriter fwriter = new FileWriter(pathBuilder.toString(), true);

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
