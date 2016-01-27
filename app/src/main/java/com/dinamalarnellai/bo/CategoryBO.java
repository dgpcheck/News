package com.dinamalarnellai.bo;

import java.io.Serializable;

/**
 * Created by welcome on 20-12-2015.
 */
public class CategoryBO implements Serializable{

    private int categoryId;
    private String categoryName;
    private String categoryImage;

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    @Override
    public String toString() {
        return categoryName.toString();
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryImage() {
        return categoryImage;
    }

    public void setCategoryImage(String categoryImage) {
        this.categoryImage = categoryImage;
    }
}
