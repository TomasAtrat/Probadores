package com.smartstore.probadores.ui.backend.repositories;

import com.smartstore.probadores.ui.backend.data.entity.Barcode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BarcodeRepository extends JpaRepository<Barcode, String> {
}