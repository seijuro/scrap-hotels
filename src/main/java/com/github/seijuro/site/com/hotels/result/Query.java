package com.github.seijuro.site.com.hotels.result;

import com.github.seijuro.site.com.hotels.data.Destination;
import com.github.seijuro.site.com.hotels.property.QueryProperty;
import com.google.gson.annotations.SerializedName;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@EqualsAndHashCode
public class Query {
    /**
     * Instance Properties
     */
    @Getter
    @Setter
    @SerializedName(QueryProperty.Destination)
    private Destination destination = null;
}
