package com.smartstore.probadores.ui.backend.data.dto.exchange;

public class Root{
    public String base;

    public int timestamp;

    public Rates rates;

    public Rates getRates() {
        return rates;
    }
}