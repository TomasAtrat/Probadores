package com.smartstore.probadores.backend.microservices.product.services;

import com.smartstore.probadores.backend.data.entity.Product;
import com.smartstore.probadores.backend.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

}
