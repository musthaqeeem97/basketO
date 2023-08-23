package com.example.basketo.shopclient.controller;


import com.example.basketo.shopadmin.product.model.Product;
import com.example.basketo.shopadmin.product.model.Review;
import com.example.basketo.shopadmin.product.repository.ProductRepository;
import com.example.basketo.shopadmin.product.repository.ReviewRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@Controller
public class ProductReviewController {

    private final ProductRepository productRepository;
    private final ReviewRepository reviewRepository;

    public ProductReviewController(ProductRepository productRepository, ReviewRepository reviewRepository) {
        this.productRepository = productRepository;
        this.reviewRepository = reviewRepository;
    }

   
}
