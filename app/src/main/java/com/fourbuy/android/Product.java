package com.fourbuy.android;

/**
 * Created by 3164 on 10-12-2015.
 */
public class Product {

    private int entity_id;
    private String product_id;
    private String sku;
    private String type_id;
    private String name;
    private String description;
    private String short_description;
    private String image_url;
    private Double final_price_with_tax;
    private String price;

    public int getEntity_id() {
        return entity_id;
    }

    public void setEntity_id(int entity_id) {
        this.entity_id = entity_id;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getType_id() {
        return type_id;
    }

    public void setType_id(String type_id) {
        this.type_id = type_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getShort_description() {
        return short_description;
    }

    public void setShort_description(String short_description) {
        this.short_description = short_description;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public Double getFinal_price_with_tax() {
        return final_price_with_tax;
    }

    public void setFinal_price_with_tax(Double final_price_with_tax) {
        this.final_price_with_tax = final_price_with_tax;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Product) {
            return getProduct_id().equals(((Product) o).getProduct_id());
        }
        return super.equals(o);
    }

    public int hashCode() {
        return getProduct_id().hashCode();
    }

    @Override
    public String toString() {
        return getProduct_id();
    }
}
