package com.smartstore.probadores.ui.backend.repositories;

import com.smartstore.probadores.ui.backend.data.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, String> {

    Optional<Product> findById(Integer id);

}