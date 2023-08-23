package com.example.basketo.shopclient.service;

import com.example.basketo.shopadmin.product.model.Product;
import com.example.basketo.shopadmin.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductViewServiceImpl {

	private final ProductRepository productRepository;
	
	public Product findProductId(UUID id) {
		return productRepository.findById(id).get();
	}
}
