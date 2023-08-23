package com.example.basketo.shopadmin.product.service;

import com.example.basketo.shopadmin.product.model.Category;
import com.example.basketo.shopadmin.product.model.CategoryImage;
import com.example.basketo.shopadmin.product.model.ProductImage;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface CategoryImageService {
    CategoryImage saveImage(CategoryImage image);

    void deleteImage(CategoryImage deletedImage);

    void deleteImageById(UUID id);

    ProductImage uploadImage(MultipartFile image, Category category);
}
