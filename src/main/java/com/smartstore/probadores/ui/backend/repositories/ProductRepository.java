package com.smartstore.probadores.ui.backend.repositories;

import com.smartstore.probadores.ui.backend.data.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, String> {

}