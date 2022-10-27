package com.smartstore.probadores.ui.backend.data.entity;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "product")
public class Product {

    @Column(name = "id", nullable = false)
    private int id;

    @Id
    @Column(name = "code", nullable = false)
    private String code;

    @Column(name = "description")
    private String description;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    private Category categoryId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "model_id")
    private Model modelId;

    @Column(name = "brand")
    private String brand;

    @Column(name = "price", precision = 10)
    private BigDecimal price;

    @Lob
    @Column(name = "picture", nullable = false, length=100000)
    private byte[] picture;

    @Column(name = "min_quantity")
    private Integer minQuantity;

    @Column(name = "resupply_quantity")
    private Integer resupplyQuantity;

    public String getId() {
        return code;
    }

    public void setId(String id) {
        this.code = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Model getModelId() {
        return modelId;
    }

    public void setModelId(Model modelId) {
        this.modelId = modelId;
    }

    public Category getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Category categoryId) {
        this.categoryId = categoryId;
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