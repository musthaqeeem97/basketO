package com.example.basketo.shopadmin.product.service;

import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import com.example.basketo.shopadmin.product.model.ProductImage;
import com.example.basketo.shopadmin.product.model.Product;



public interface ProductImageService {


    ProductImage saveImage(ProductImage image);

    void deleteImage(ProductImage deletedImage);

    void deleteImageById(UUID id);
    
    ProductImage uploadImage(MultipartFile image,Product product);
}
