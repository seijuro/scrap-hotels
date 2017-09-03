package com.github.seijuro.search;

import com.github.seijuro.search.query.Date;
import com.github.seijuro.search.query.Destination;
import com.github.seijuro.search.query.Lodging;
import com.github.seijuro.search.query.Sort;

import java.util.List;

public interface SearchURL {
    public abstract String toURL();

    public abstract Destination getDestination();
    public abstract Sort getSort();
    public abstract int getPage();
    public abstract Date getStartDate();
    public abstract Date getEndDate();
    public abstract <T extends Lodging> List<T> getLodgings();
}
