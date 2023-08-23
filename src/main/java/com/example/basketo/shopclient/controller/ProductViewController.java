package com.example.basketo.shopclient.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.basketo.shopadmin.product.model.Product;
import com.example.basketo.shopadmin.product.model.Review;
import com.example.basketo.shopadmin.product.repository.ReviewRepository;
import com.example.basketo.shopadmin.product.service.ProductService;
import com.example.basketo.shopclient.service.ProductViewServiceImpl;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductViewController {

	
	private final ProductViewServiceImpl productViewService;
	private final ProductService productService;
	private final ReviewRepository reviewRepository;
	
	
	@GetMapping("/{id}")
	public String getProductDetailsView(@PathVariable("id") UUID id, Model model) {
		
		Product product = productViewService.findProductId(id);
		System.out.println("product rating:"+ product.getRating());
		model.addAttribute("product", product);
		model.addAttribute("rating", product.getRating());
		
		List<Review> reviews = reviewRepository.findByProduct(product);
		System.out.println(" reviews count: "+reviews.size());
		if (!reviews.isEmpty()) {
			model.addAttribute("reviews", reviews);
		    model.addAttribute("reviewCount", reviews.size());
		}
		
	   
		
		
		return "front-end/product-view";
	}
	
}
