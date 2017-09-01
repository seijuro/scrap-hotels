package com.github.seijuro.site.com.hotels.result;

import com.github.seijuro.site.com.hotels.property.ResultProperty;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

@ToString
public class Result {
    @Getter
    @SerializedName(ResultProperty.Id)
    private Integer id = null;
    @Getter
    @SerializedName(ResultProperty.SupplierHotelId)
    private Integer supplierHotelId = null;
    @Getter
    @SerializedName(ResultProperty.Name)
    private String name = StringUtils.EMPTY;
    @Getter
    @SerializedName(ResultProperty.Address)
    private Address address = null;
    @Getter
    @SerializedName(ResultProperty.URLs)
    private URLs urLs = null;
    @Getter
    @SerializedName(ResultProperty.GuestReviews)
    private GuestReviews guestReviews = null;
    @Getter
    @SerializedName(ResultProperty.RatePlan)
    private RatePlan ratePlan = null;
    @Getter
    @SerializedName(ResultProperty.Deals)
    private Deals deals = null;
    @Getter
    @SerializedName(ResultProperty.Badging)
    private Badging badging = null;
    @Getter
    @SerializedName(ResultProperty.Sponsored)
    private Sponsored sponsored = null;
}
