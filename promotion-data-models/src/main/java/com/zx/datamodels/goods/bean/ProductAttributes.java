package com.zx.datamodels.goods.bean;

import java.io.Serializable;
import java.util.List;

public class ProductAttributes implements Serializable{
    private String attributeName;
    private List<String> attributeValues;
    public String getAttributeName() {
        return attributeName;
    }
    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }
    public List<String> getAttributeValues() {
        return attributeValues;
    }
    public void setAttributeValues(List<String> attributeValues) {
        this.attributeValues = attributeValues;
    }
    
    
    
}
