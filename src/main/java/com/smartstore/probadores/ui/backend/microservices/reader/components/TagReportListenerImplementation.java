package com.smartstore.probadores.ui.backend.microservices.reader.components;

import com.impinj.octane.ImpinjReader;
import com.impinj.octane.Tag;
import com.impinj.octane.TagReport;
import com.impinj.octane.TagReportListener;
import com.smartstore.probadores.ui.backend.data.dto.ExchangeType;
import com.smartstore.probadores.ui.backend.data.entity.Product;
import com.smartstore.probadores.ui.backend.data.entity.ProductInFittingRoom;
import com.smartstore.probadores.ui.backend.microservices.product.services.ProductService;
import com.smartstore.probadores.ui.backend.microservices.reader.components.EPCSchemaStrategy;
import com.smartstore.probadores.ui.backend.microservices.reader.components.SGTIN96Schema;
import com.smartstore.probadores.ui.views.sistemadeprobadores.SistemadeprobadoresView;
import com.smartstore.probadores.ui.views.utils.InMemoryVariables;
import com.vaadin.flow.component.UI;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class TagReportListenerImplementation implements TagReportListener {

    private final ProductService productService;
    private UI ui;
    private static List<Product> products;

    public TagReportListenerImplementation(ProductService productService, UI ui) {
        this.productService = productService;
        this.ui = ui;
        products = new ArrayList<>();
    }

    @Override
    public void onTagReported(ImpinjReader reader, TagReport report) {
        List<Tag> tags = report.getTags();

        EPCSchemaStrategy schemaStrategy = new SGTIN96Schema();

        for (Tag t : tags) {
            System.out.print(" EPC: " + t.getEpc().toString());
            var barcode = schemaStrategy.getBarcodeFromEPC(t.getEpc().toHexString());
            System.out.print(" Barcode: " + barcode.getId());

            System.out.println();
            var product = productService.findProductByBarcode(barcode.getId());

            if (products.stream().anyMatch(i -> i.getId().equals(product.getId())))
                products = new ArrayList<>();
            else {
                products.add(product);
            }

            System.out.println("Producto : " + product.getDescription());

            InsertLog(product);
        }

        SistemadeprobadoresView.getLayoutUI().access(() -> {
            SistemadeprobadoresView.setProductsToCombobox(products);
        });
    }

    private void InsertLog(Product product) {
        var readerInBranch = InMemoryVariables.readerAntennaInBranch;

        ProductInFittingRoom productInFittingRoom = new ProductInFittingRoom();
        productInFittingRoom.setProductCode(product);
        productInFittingRoom.setFittingRoom(readerInBranch.getFittingRoom());
        productInFittingRoom.setBranch(readerInBranch.getBranch());
        productInFittingRoom.setLogAddDate(new Date());

        productService.insertLog(productInFittingRoom);
    }
}