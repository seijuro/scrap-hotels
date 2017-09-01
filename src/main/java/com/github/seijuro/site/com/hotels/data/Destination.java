package com.github.seijuro.site.com.hotels.data;

import com.github.seijuro.site.com.hotels.property.DestinationProperty;
import com.google.gson.annotations.SerializedName;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

@ToString
@EqualsAndHashCode
public class Destination {
    /**
     * Instance Properties
     */
    @Getter
    @Setter
    @SerializedName(DestinationProperty.Id)
    private String id = StringUtils.EMPTY;

    @Getter
    @Setter
    @SerializedName(DestinationProperty.Value)
    private String value = StringUtils.EMPTY;

    @Getter
    @Setter
    @SerializedName(DestinationProperty.ShortName)
    private String shortName = StringUtils.EMPTY;

    @Getter
    @Setter
    @SerializedName(DestinationProperty.ResolvedLocation)
    private String resolvedLocation = StringUtils.EMPTY;

    @Getter
    @Setter
    @SerializedName(DestinationProperty.DestinationType)
    private String destinationType = StringUtils.EMPTY;
}
