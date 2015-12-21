package com.fourbuy.android.model;

/**
 * Created by Santosh on 12/20/2015.
 */
public class Country {

    String country_id;
    String iso2_code;
    String iso3_code;
    String name;

    public Country(String country_id, String iso2_code, String iso3_code, String name) {
        this.country_id = country_id;
        this.iso2_code = iso2_code;
        this.iso3_code = iso3_code;
        this.name = name;
    }

}
