package com.smartstore.probadores.ui.backend.microservices.product.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartstore.probadores.ui.backend.data.dto.ExchangeType;
import com.smartstore.probadores.ui.backend.data.dto.exchange.Root;
import com.smartstore.probadores.ui.backend.data.entity.Category;
import com.smartstore.probadores.ui.backend.data.entity.Product;
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

    @Autowired
    public ProductService(ProductRepository productRepository){
        this.productRepository = productRepository;
    }

    public List<Product> findAll(){
        return this.productRepository.findAll();
    }

    public Optional<Product> findById(int id) { return this.productRepository.findById(id); }

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

    public List<Product> findByCategoryId(Category categoryId) { return this.productRepository.findByCategoryId(categoryId); }
}
