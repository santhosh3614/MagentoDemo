package com.fourbuy.android.model;

/**
 * Created by Santosh on 12/19/2015.
 */
public class AddressInfo {

    String mode;
    String address_id;
    String created_at;
    String updated_at;
    String customer_id;
    int save_in_address_book;
    String customer_address_id;
    String address_type;
    String email;
    String prefix;
    String firstname;
    String middlename;
    String lastname;
    String suffix;
    String company;
    String street;
    public String city;
    public String region;
    String region_id;
    String postcode;
    String country_id;
    String telephone;
    String fax;
    int same_as_billing;
    int free_shipping;

    public String getAddress_id() {
        return address_id;
    }

    public void setAddress_id(String address_id) {
        this.address_id = address_id;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(String customer_id) {
        this.customer_id = customer_id;
    }

    public int getSave_in_address_book() {
        return save_in_address_book;
    }

    public void setSave_in_address_book(int save_in_address_book) {
        this.save_in_address_book = save_in_address_book;
    }

    public String getCustomer_address_id() {
        return customer_address_id;
    }

    public void setCustomer_address_id(String customer_address_id) {
        this.customer_address_id = customer_address_id;
    }

    public String getAddress_type() {
        return address_type;
    }

    public void setAddress_type(String address_type) {
        this.address_type = address_type;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getMiddlename() {
        return middlename;
    }

    public void setMiddlename(String middlename) {
        this.middlename = middlename;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getRegion_id() {
        return region_id;
    }

    public void setRegion_id(String region_id) {
        this.region_id = region_id;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getCountry_id() {
        return country_id;
    }

    public void setCountry_id(String country_id) {
        this.country_id = country_id;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public int getSame_as_billing() {
        return same_as_billing;
    }

    public void setSame_as_billing(int same_as_billing) {
        this.same_as_billing = same_as_billing;
    }

    AddressInfo getAddressInfo(){
        return null;
    }


}
