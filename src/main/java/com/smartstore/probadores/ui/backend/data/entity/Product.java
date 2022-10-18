package com.smartstore.probadores.ui.backend.data.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "product")
public class Product {
    @Id
    @Column(name = "code", nullable = false)
    private String id;

    @Column(name = "description")
    private String description;

    @Column(name = "category")
    private String category;

    @Column(name = "model")
    private String model;

    @Column(name = "brand")
    private String brand;

    @Column(name = "price", precision = 10)
    private BigDecimal price;

    @Column(name = "picture")
    private byte[] picture;

    @Column(name = "min_quantity")
    private Integer minQuantity;

    @Column(name = "resupply_quantity")
    private Integer resupplyQuantity;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public byte[] getPicture() {
        return picture;
    }

    public void setPicture(byte[] picture) {
        this.picture = picture;
    }

    public Integer getMinQuantity() {
        return minQuantity;
    }

    public void setMinQuantity(Integer minQuantity) {
        this.minQuantity = minQuantity;
    }

    public Integer getResupplyQuantity() {
        return resupplyQuantity;
    }

    public void setResupplyQuantity(Integer resupplyQuantity) {
        this.resupplyQuantity = resupplyQuantity;
    }

}