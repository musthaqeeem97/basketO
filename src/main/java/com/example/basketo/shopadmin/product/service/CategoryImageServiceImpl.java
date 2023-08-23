package com.example.basketo.shopadmin.product.service;

import com.example.basketo.shopadmin.product.model.Category;
import com.example.basketo.shopadmin.product.model.CategoryImage;
import com.example.basketo.shopadmin.product.model.ProductImage;
import com.example.basketo.shopadmin.product.repository.CategoryImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryImageServiceImpl implements CategoryImageService{

    private final CategoryImageRepository categoryImageRepository;
    
    @Override
    public CategoryImage saveImage(CategoryImage image) { 
    	
        return categoryImageRepository.save(image);
    }


        @Override
    public void deleteImage(CategoryImage deletedImage) {

        	categoryImageRepository.delete(deletedImage);
    }

    @Override
    public void deleteImageById(UUID id) {

    	categoryImageRepository.deleteById(id);
    }

    @Override
    public ProductImage uploadImage(MultipartFile image, Category category) {
        return null;
    }
    
  
    public void deleteImageByCategory( Category category) {

    	categoryImageRepository.deleteAllByCategory(category);
    }


	public List<CategoryImage> findImagebyCatgeoryId(UUID id) {
		
		return categoryImageRepository.findAllByCategoryId(id);
		
	}
}
