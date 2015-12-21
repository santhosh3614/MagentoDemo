package com.fourbuy.android.model;

import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;

import java.util.Vector;

/**
 * Created by Santosh on 12/19/2015.
 */
public class CartInfo {

    AddressInfo shippingAddress;
    String store_id;
    String created_at;
    String updated_at;
    String converted_at;
    int  quote_id;
    int is_active;
    int is_virtual;
    int is_multi_shipping;
    double items_count;
    double items_qty;
    String orig_order_id;
    String store_to_base_rate;
    String store_to_quote_rate;
    String base_currency_code;
    String store_currency_code;
    String quote_currency_code;
    String grand_total;
    String base_grand_total;
    String checkout_method;
    public Customer customer;
    String shipping_method;
    String shipping_description;
    double weight;
    public boolean havebillingAddress;

    public static CartInfo getCartInfo(Object result){
        CartInfo cartInfo=null;
        if(result instanceof Vector){
            SoapObject res= (SoapObject) ((Vector)result).get(1);
            cartInfo=new CartInfo();
            for (int i = 0; i < res.getPropertyCount(); i++) {
                PropertyInfo propertyInfo = new PropertyInfo();
                res.getPropertyInfo(i, propertyInfo);
                if(propertyInfo.name.equals("store_id")){
                    cartInfo.store_id=propertyInfo.getValue().toString();
                }else if(propertyInfo.name.equals("created_at")){
                    cartInfo.created_at=propertyInfo.getValue().toString();
                }else if(propertyInfo.name.equals("updated_at")){
                    cartInfo.updated_at=propertyInfo.getValue().toString();
                }else if(propertyInfo.name.equals("quote_id")){
                    cartInfo.quote_id=Integer.parseInt(propertyInfo.getValue().toString());
                }else if(propertyInfo.name.equals("is_active")){
                    cartInfo.is_active=Integer.parseInt(propertyInfo.getValue().toString());
                }else if(propertyInfo.name.equals("is_virtual")){
                    cartInfo.is_virtual=Integer.parseInt(propertyInfo.getValue().toString());
                }else if(propertyInfo.name.equals("is_multi_shipping")){
                    cartInfo.is_multi_shipping=Integer.parseInt(propertyInfo.getValue().toString());
                }else if(propertyInfo.name.equals("items_count")){
                    cartInfo.items_count=Double.parseDouble(propertyInfo.getValue().toString());
                }else if(propertyInfo.name.equals("items_qty")){
                    cartInfo.items_qty=Double.parseDouble(propertyInfo.getValue().toString());
                }else if(propertyInfo.name.equals("store_to_base_rate")){
                    cartInfo.store_to_base_rate=propertyInfo.getValue().toString();
                }else if(propertyInfo.name.equals("store_to_quote_rate")){
                    cartInfo.store_to_quote_rate=propertyInfo.getValue().toString();
                }else if(propertyInfo.name.equals("base_currency_code")){
                    cartInfo.base_currency_code=propertyInfo.getValue().toString();
                }else if(propertyInfo.name.equals("store_currency_code")){
                    cartInfo.store_currency_code=propertyInfo.getValue().toString();
                }else if(propertyInfo.name.equals("quote_currency_code")){
                    cartInfo.quote_currency_code=propertyInfo.getValue().toString();
                }else if(propertyInfo.name.equals("grand_total")){
                    cartInfo.grand_total=propertyInfo.getValue().toString();
                }else if(propertyInfo.name.equals("base_grand_total")){
                    cartInfo.base_grand_total=propertyInfo.getValue().toString();
                }else if(propertyInfo.name.equals("checkout_method")){
                    cartInfo.checkout_method=propertyInfo.getValue().toString();
                }else if(propertyInfo.name.equals("orig_order_id")){
                    cartInfo.orig_order_id=propertyInfo.getValue().toString();
                }else if(propertyInfo.name.equals("customer_id")){
                    String cusId = propertyInfo.getValue().toString();
                    if(!cusId.equals("0")){
                        cartInfo.customer = new Customer();
                        cartInfo.customer.setCustomerId(Integer.parseInt(cusId));
                    }
                }else if(propertyInfo.name.equals("customer_email")){
                    if(cartInfo.customer !=null){
                        cartInfo.customer.setEmailId(propertyInfo.getValue().toString());
                    }
                }else if(propertyInfo.name.equals("customer_firstname")){
                    if(cartInfo.customer !=null){
                        cartInfo.customer.setFirstName(propertyInfo.getValue().toString());
                    }
                }else if(propertyInfo.name.equals("customer_firstname")){
                    if(cartInfo.customer !=null){
                        cartInfo.customer.setFirstName(propertyInfo.getValue().toString());
                    }
                }else if(propertyInfo.name.equals("customer_middlename")){
                    if(cartInfo.customer !=null){
                        cartInfo.customer.setFirstName(propertyInfo.getValue().toString());
                    }
                }else if(propertyInfo.name.equals("customer_lastname")){
                    if(cartInfo.customer !=null){
                        cartInfo.customer.setFirstName(propertyInfo.getValue().toString());
                    }
                }
//                else if(propertyInfo.name.equals("shipping_address")){
//                    SoapObject soapObject= (SoapObject) propertyInfo.getValue();
//                    propertyInfo = new PropertyInfo();
//                    res.getPropertyInfo(i, propertyInfo);
//                    Object value = propertyInfo.getValue();
//                    if(value instanceof Vector){
//                        for (int j = 0; j < ((Vector) value).size(); j++) {
//                           cartInfo.shippingAddress=new AddressInfo();
//
//                        }
//                    }
//                }
                else if(propertyInfo.name.equals("billing_address")){
//                    SoapObject soapObject= (SoapObject) propertyInfo.getValue();
//                    Object address_id = soapObject.getProperty("address_id");
//                    if(address_id!=null){
//                        cartInfo.havebillingAddress=true;
//                    }
                }
            }
        }
        return cartInfo;
    }
}
