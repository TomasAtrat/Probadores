package com.smartstore.probadores.backend.repositories;

import com.smartstore.probadores.backend.data.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, String> {

    Optional<Product> findById(Integer id);

}