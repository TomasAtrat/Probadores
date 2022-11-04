package com.smartstore.probadores.ui.backend.data.dto.exchange;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Rates{
    @JsonProperty("ARS")
    public ARS aRS;

    @JsonProperty("BRL")
    public BRL bRL;

    @JsonProperty("EUR")
    public EUR eUR;

    @JsonProperty("USD")
    public USD uSD;
}