package com.fourbuy.android;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 3164 on 09-12-2015.
 */
public class Category implements Parcelable {

    private int categoryId;

    public Category() {
    }

    protected Category(Parcel in) {
        categoryId = in.readInt();
        parentId = in.readInt();
        name = in.readString();
        categoryList = in.createTypedArrayList(Category.CREATOR);
    }

    public static final Creator<Category> CREATOR = new Creator<Category>() {
        @Override
        public Category createFromParcel(Parcel in) {
            return new Category(in);
        }

        @Override
        public Category[] newArray(int size) {
            return new Category[size];
        }
    };

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    public List<Category> getCategoryList() {
        return categoryList;
    }

    public void addCategory(Category category) {
        this.categoryList.add(category);
    }

    private int parentId;
    private String name;
    private boolean isActive;
    private List<Category> categoryList = new ArrayList<>();

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(categoryId);
        dest.writeInt(parentId);
        dest.writeString(name);
        dest.writeTypedList(categoryList);
    }
}
