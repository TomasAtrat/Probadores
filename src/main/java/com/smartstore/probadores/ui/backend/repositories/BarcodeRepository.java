package com.smartstore.probadores.ui.backend.repositories;

import com.smartstore.probadores.ui.backend.data.entity.Barcode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BarcodeRepository extends JpaRepository<Barcode, String> {
    @Query("SELECT b from Barcode b where b.productCode.code = ?1 and b.id in (SELECT sto.barcodeBarcode from Stock sto WHERE sto.branch.id = ?2)")
    List<Barcode> getProductVariantsInStock(String productCode, Long branchId);

    @Query("SELECT b from Barcode b where b.productCode.code = ?1 and b.colour = ?2 and b.size = ?3 and b.id in (SELECT sto.barcodeBarcode from Stock sto WHERE sto.branch.id = ?4)")
    Barcode findByProductCodeAndColourAndSizeAndBranch(String product, String colour, String size, Long branchId);
}