package com.example.basketo.shopadmin.product.repository;

import java.util.UUID;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.basketo.shopadmin.product.model.Product;
import com.example.basketo.shopadmin.product.model.Thumbnail;

public interface ThumbnailRepository  extends JpaRepository<Thumbnail, UUID>{
	
	    @Transactional
	    void deleteAllByProduct(Product product);
}
