package com.smartstore.probadores.ui.backend.microservices.product.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartstore.probadores.ui.backend.data.dto.ExchangeType;
import com.smartstore.probadores.ui.backend.data.dto.exchange.Root;
import com.smartstore.probadores.ui.backend.data.entity.Barcode;
import com.smartstore.probadores.ui.backend.data.entity.Product;
import com.smartstore.probadores.ui.backend.data.entity.ProductInFittingRoom;
import com.smartstore.probadores.ui.backend.repositories.BarcodeRepository;
import com.smartstore.probadores.ui.backend.repositories.ProductInFittingRoomRepository;
import com.smartstore.probadores.ui.backend.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private ProductRepository productRepository;
    private BarcodeRepository barcodeRepository;
    private ProductInFittingRoomRepository productInFittingRoomRepository;

    @Autowired
    public ProductService(ProductRepository productRepository,
                          BarcodeRepository barcodeRepository,
                          ProductInFittingRoomRepository productInFittingRoomRepository){
        this.productRepository = productRepository;
        this.barcodeRepository = barcodeRepository;
        this.productInFittingRoomRepository = productInFittingRoomRepository;
    }

    public List<Product> findAll(){
        return this.productRepository.findAll();
    }

    public Optional<Product> findById(int id) { return this.productRepository.findById(id); }

    public Product findProductByBarcode(String barcode) {
        var barcodeObj = this.barcodeRepository.findById(barcode);
        return barcodeObj.get().getProductCode();
    }

    public void insertLog(ProductInFittingRoom productInFittingRoom) {
        productInFittingRoomRepository.save(productInFittingRoom);
    }

    public List<Barcode> getProductVariantsInStock(String productCode, Long branchId) {
        return barcodeRepository.getProductVariantsInStock(productCode, branchId);
    }

    public Barcode getBarcodeByProductColourAndSize(Product product, String colour, String size, Long branchId) {
        return barcodeRepository.findByProductCodeAndColourAndSizeAndBranch(product.getId(), colour, size, branchId);
    }
    public ExchangeType GetExchangeType(Double price) throws IOException {
        ExchangeType result = new ExchangeType();

        URL url = new URL("https://cotizaciones-brou.herokuapp.com/api/currency/latest");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Content-Type", "application/json");

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder content = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();

        con.disconnect();

        ObjectMapper om = new ObjectMapper();
        Root root = om.readValue(content.toString(), Root.class);

        result.setUruguayanPeso(price);
        result.setBrazilianReal(price / root.getRates().bRL.getBuy());
        result.setArgentinianPeso(price / root.getRates().aRS.getBuy());
        result.setDollar(price / root.getRates().uSD.getBuy());

        return result;
    }
}
