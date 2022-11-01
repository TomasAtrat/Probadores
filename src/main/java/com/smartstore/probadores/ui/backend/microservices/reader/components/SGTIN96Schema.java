package com.smartstore.probadores.ui.backend.microservices.reader.components;

import com.smartstore.probadores.ui.backend.data.entity.Barcode;
import org.epctagcoder.parse.SGTIN.*;
import org.epctagcoder.result.SGTIN;

public class SGTIN96Schema implements EPCSchemaStrategy {
    @Override
    public Barcode getBarcodeFromEPC(String epc) throws RuntimeException {
        try {
            ParseSGTIN parseSGTIN = ParseSGTIN.Builder()
                    .withRFIDTag(epc)
                    .build();

            SGTIN sgtin = parseSGTIN.getSGTIN();

            String barcode = sgtin.getCompanyPrefix() + sgtin.getItemReference() + sgtin.getCheckDigit();

            var barcodeObj = new Barcode();
            barcodeObj.setId(barcode);

            return barcodeObj;
        } catch (Exception e) {
            throw new RuntimeException("Problems while decoding tag " + epc);
        }
    }
}