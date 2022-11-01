package com.smartstore.probadores.ui.backend.data.entity;

import javax.persistence.*;

@Entity
@Table(name = "barcode")
public class Barcode {
    @Id
    @Column(name = "barcode", nullable = false)
    private String id;

    @Column(name = "colour", length = 64)
    private String colour;

    @Column(name = "size", length = 16)
    private String size;

    @Column(name = "description_1")
    private String description1;

    @Column(name = "description_2")
    private String description2;

    @Column(name = "description_3")
    private String description3;

    @Column(name = "description_4")
    private String description4;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_code")
    private Product productCode;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getDescription1() {
        return description1;
    }

    public void setDescription1(String description1) {
        this.description1 = description1;
    }

    public String getDescription2() {
        return description2;
    }

    public void setDescription2(String description2) {
        this.description2 = description2;
    }

    public String getDescription3() {
        return description3;
    }

    public void setDescription3(String description3) {
        this.description3 = description3;
    }

    public String getDescription4() {
        return description4;
    }

    public void setDescription4(String description4) {
        this.description4 = description4;
    }

    public Product getProductCode() {
        return productCode;
    }

    public void setProductCode(Product productCode) {
        this.productCode = productCode;
    }

}