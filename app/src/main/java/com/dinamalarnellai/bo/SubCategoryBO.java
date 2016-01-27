package com.dinamalarnellai.bo;

import java.io.Serializable;

/**
 * Created by gnanaprakasam.d on 29-12-2015.
 */
public class SubCategoryBO implements Serializable {

    private int subCategoryId;
    private String subCategoryName;

    public int getSubCategoryId() {
        return subCategoryId;
    }

    public void setSubCategoryId(int subCategoryId) {
        this.subCategoryId = subCategoryId;
    }

    public String getSubCategoryName() {
        return subCategoryName;
    }

    public void setSubCategoryName(String subCategoryName) {
        this.subCategoryName = subCategoryName;
    }

    @Override
    public String toString() {
        return subCategoryName.toString();
    }
}
