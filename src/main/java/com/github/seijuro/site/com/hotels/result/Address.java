package com.github.seijuro.site.com.hotels.result;

import com.github.seijuro.site.com.hotels.property.AddressProperty;
import com.google.gson.annotations.SerializedName;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

@ToString
public class Address {
    @SerializedName(AddressProperty.StreetAddress)
    private String streetAddress = StringUtils.EMPTY;
    @SerializedName(AddressProperty.ExtendedAddress)
    private String extendedAddress = StringUtils.EMPTY;
    @SerializedName(AddressProperty.Locality)
    private String locality = StringUtils.EMPTY;
    @SerializedName(AddressProperty.PostalCode)
    private String postalCode = StringUtils.EMPTY;
    @SerializedName(AddressProperty.Region)
    private String region = StringUtils.EMPTY;
    @SerializedName(AddressProperty.CountryName)
    private String countryName = StringUtils.EMPTY;
    @SerializedName(AddressProperty.CountryCode)
    private String countryCode = StringUtils.EMPTY;

    public String toText() {
        StringBuffer sb = new StringBuffer(streetAddress);
        sb.append(", ").append(locality);

        if (Objects.nonNull(region) &&
                region.length() > 0) {
            sb.append(", ").append(region);
        }

        if (Objects.nonNull(postalCode) &&
                postalCode.length() > 0) {
            sb.append(", ").append(postalCode);
        }

        sb.append(" ").append(countryName);

        return sb.toString();
    }
}
