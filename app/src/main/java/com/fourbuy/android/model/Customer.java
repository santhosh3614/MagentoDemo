package com.fourbuy.android.model;

/**
 * Created by Santosh on 12/19/2015.
 */
public class Customer {

    private String emailId;
    private String password;
    private String firstName;
    private String lastName;
    private String middleName;
    private String companyName;
    private int customerId;
    private AddressInfo addressInfo;
    private AddressInfo shippingAddressInfo;


    public Customer(int customerId,String emailId, String password, String firstName, String lastName,
                    String middleName) {
        this.customerId=customerId;
        this.emailId = emailId;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.middleName = middleName;
    }

    public Customer(){
    }
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public void setCustomerId(int customerId) {
        this.customerId=customerId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public AddressInfo getAddressInfo() {
        return addressInfo;
    }

    public void setAddressInfo(AddressInfo addressInfo) {
        this.addressInfo = addressInfo;
    }

    public AddressInfo getShippingAddressInfo() {
        return shippingAddressInfo;
    }

    public void setShippingAddressInfo(AddressInfo shippingAddressInfo) {
        this.shippingAddressInfo = shippingAddressInfo;
    }
}
