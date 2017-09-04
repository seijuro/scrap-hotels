package com.github.seijuro.site.com.agoda;

import com.github.seijuro.site.com.agoda.query.Destination;
import com.github.seijuro.site.com.agoda.query.Lodging;
import com.github.seijuro.site.com.agoda.query.Sort;
import lombok.extern.log4j.Log4j2;
import org.junit.Test;

import java.util.Arrays;

@Log4j2
public class SearchURLTest {
    @Test
    public void testSearchURL() {
        SearchURL.Builder builder = new SearchURL.Builder();
        builder.setCheckIn(2017, 11,15);
        builder.setCheckOut(2017, 11,16);
        builder.setLodgings(Arrays.asList(Lodging.HOTEL, Lodging.RESORT));

        for (Destination destination : Destination.values()) {
            builder.setDestination(destination);

            for (Sort sort : Sort.values()) {
                builder.setSort(sort);

                //  log
                log.debug("destination : {}, sort : {}, requestURL : {}", destination.getLabel(), sort.getLabel(), builder.build().toURL());
            }
        }
    }
}