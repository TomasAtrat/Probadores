package com.smartstore.probadores.ui.backend.data.entity;

import javax.persistence.*;

@Entity
@Table(name = "reader")
public class Reader {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "version", nullable = false)
    private Integer version;

    @Column(name = "rssi")
    private Float rssi;

    @Column(name = "alias")
    private String alias;

    @Column(name = "antena_power")
    private Float antenaPower;

    @Column(name = "fl_active")
    private Boolean flActive;

    @Column(name = "name")
    private String name;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "module_id", nullable = false)
    private Module module;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Float getRssi() {
        return rssi;
    }

    public void setRssi(Float rssi) {
        this.rssi = rssi;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public Float getAntenaPower() {
        return antenaPower;
    }

    public void setAntenaPower(Float antenaPower) {
        this.antenaPower = antenaPower;
    }

    public Boolean getFlActive() {
        return flActive;
    }

    public void setFlActive(Boolean flActive) {
        this.flActive = flActive;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Module getModule() {
        return module;
    }

    public void setModule(Module module) {
        this.module = module;
    }

}