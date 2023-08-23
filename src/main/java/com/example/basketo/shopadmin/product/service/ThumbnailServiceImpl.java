package com.example.basketo.shopadmin.product.service;

import java.util.UUID;

import com.example.basketo.shopadmin.product.repository.ThumbnailRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.basketo.shopadmin.product.model.Product;
import com.example.basketo.shopadmin.product.model.ProductImage;
import com.example.basketo.shopadmin.product.model.Thumbnail;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ThumbnailServiceImpl implements ThumbnailService{
	
	private final ThumbnailRepository thumbnailRepository;

	@Override
	public Thumbnail save(Thumbnail thumbnail) {
		// TODO Auto-generated method stub
		return thumbnailRepository.save(thumbnail) ;
	}

	@Override
	public void delete(Thumbnail deletedThumbnail) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteThumbnailById(UUID id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ProductImage uploadImage(MultipartFile image, Product product) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void deleteThumbnailByProduct(Product product) {
		thumbnailRepository.deleteAllByProduct(product);
    }

	
}
