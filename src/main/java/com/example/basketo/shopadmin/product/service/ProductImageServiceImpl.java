package com.example.basketo.shopadmin.product.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.basketo.shopadmin.product.model.ProductImage;
import com.example.basketo.shopadmin.product.model.Product;
import com.example.basketo.shopadmin.product.repository.ProductImageRepository;

import java.util.UUID;

@Service
public class ProductImageServiceImpl implements ProductImageService {

    @Autowired
    ProductImageRepository imageRepository;

    @Override
    public ProductImage saveImage(ProductImage image) {
        return imageRepository.save(image);
    }

    @Override
    public void deleteImage(ProductImage image) {
        imageRepository.delete(image);
    }

    @Override
    public void deleteImageById(UUID id) {
        imageRepository.deleteById(id);
   
    }

	@Override
	public ProductImage uploadImage(MultipartFile image, Product product) {
		// TODO Auto-generated method stub
		return null;
	}

	public void deleteImageByProduct(Product product) {
		imageRepository.deleteAllByProduct(product);
    }
		
	

	

    


}