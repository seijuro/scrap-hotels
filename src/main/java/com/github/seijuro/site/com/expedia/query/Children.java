package com.github.seijuro.site.com.expedia.query;

import com.github.seijuro.search.query.QueryParameter;

import java.util.List;
import java.util.Objects;

public class Children implements QueryParameter {
    List<Integer> ages = null;

    /**
     * Construct
     *
     * @param ages
     */
    public Children(List<Integer> ages) {
        if (Objects.nonNull(ages) && ages.size() > 0) {
            this.ages = ages;
        }
        else {
            this.ages = null;
        }
    }

    @Override
    public String getQueryParameter() {
        if (Objects.nonNull(ages) && ages.size() > 0) {
            StringBuffer param = new StringBuffer();
            param.append("1_").append(ages.get(0));

            for (int index = 1; index < ages.size(); ++index) {
                param.append("1_").append(ages.get(index));
            }

            return param.toString();
        }

        return null;
    }
}
