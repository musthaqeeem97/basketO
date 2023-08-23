package com.example.basketo.shopadmin.product.service;

import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import com.example.basketo.shopadmin.product.model.Product;
import com.example.basketo.shopadmin.product.model.ProductImage;
import com.example.basketo.shopadmin.product.model.Thumbnail;

public interface ThumbnailService {
	    
	    ProductImage uploadImage(MultipartFile image,Product product);

		Thumbnail save(Thumbnail thumbnail);

		void delete(Thumbnail deletedThumbnail);

		void deleteThumbnailById(UUID id);
}
