package com.example.basketo.shopadmin.product.service;

import org.springframework.data.domain.Page;


import com.example.basketo.shopadmin.product.model.Category;
import java.util.Optional;
import java.util.UUID;

public interface CategoryService {
   

	
	
	Page<Category> findPaginated(int pageNo, int i, String sortField, String sortDir, String SearchTerm);

	Optional<Category> findByUuid(UUID id);

	Category findByName(String name);

	boolean update(Category category);
	
	void save(Category category);
}