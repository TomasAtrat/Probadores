package com.smartstore.probadores.ui.backend.microservices.product.services;

import com.smartstore.probadores.ui.backend.data.entity.Product;
import com.smartstore.probadores.ui.backend.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

}
