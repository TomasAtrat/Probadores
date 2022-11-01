package com.smartstore.probadores.ui.backend.microservices.reader.components;

import com.smartstore.probadores.ui.backend.data.entity.Barcode;

public interface EPCSchemaStrategy {
    Barcode getBarcodeFromEPC(String epc) throws RuntimeException;
}