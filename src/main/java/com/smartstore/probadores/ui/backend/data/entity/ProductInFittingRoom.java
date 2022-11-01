package com.smartstore.probadores.ui.backend.data.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "product_in_fitting_room")
public class ProductInFittingRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "product_code", nullable = false)
    private Product productCode;

    @Column(name = "fitting_room")
    private Integer fittingRoom;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    @Column(name = "log_add_date")
    private Date logAddDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Product getProductCode() {
        return productCode;
    }

    public void setProductCode(Product productCode) {
        this.productCode = productCode;
    }

    public Integer getFittingRoom() {
        return fittingRoom;
    }

    public void setFittingRoom(Integer fittingRoom) {
        this.fittingRoom = fittingRoom;
    }

    public Branch getBranch() {
        return branch;
    }

    public void setBranch(Branch branch) {
        this.branch = branch;
    }

    public Date getLogAddDate() {
        return logAddDate;
    }

    public void setLogAddDate(Date logAddDate) {
        this.logAddDate = logAddDate;
    }

}