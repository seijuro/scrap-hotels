package com.github.seijuro.site.com.hotels.property;

public class DestinationProperty {
    public static final String Id = "id";
    public static final String Value = "value";
    public static final String ShortName = "shortName";
    public static final String ResolvedLocation = "resolvedLocation";
    public static final String DestinationType = "destinationType";

    /**
     * Instance Properties
     */
    private final String propertyName;

    /**
     * Construct <code>DestinationProperty</code>
     */
    DestinationProperty(String name) {
        propertyName = name;
    }
}
