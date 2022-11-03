package com.smartstore.probadores.ui.backend.microservices.product.services;

import com.smartstore.probadores.ui.backend.data.entity.Barcode;
import com.smartstore.probadores.ui.backend.data.entity.Product;
import com.smartstore.probadores.ui.backend.data.entity.ProductInFittingRoom;
import com.smartstore.probadores.ui.backend.repositories.BarcodeRepository;
import com.smartstore.probadores.ui.backend.repositories.ProductInFittingRoomRepository;
import com.smartstore.probadores.ui.backend.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
