package com.example.basketo.shopclient.service;



import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.basketo.shopclient.repository.ProductListingRepository;
import com.example.basketo.shopadmin.product.model.Product;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductListingServiceImpl  {

	private final ProductListingRepository productListingRepository;
	
	public Page<Product> findPaginated(int pageNo, int offset, String sortField, String sortDir, String searchTerm) {
		Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() :
			Sort.by(sortField).descending();
		
	
		Pageable pageable = PageRequest.of(pageNo - 1, offset, sort);
		
		System.err.println(" end of productList pagination service");
		return productListingRepository.findAllBySearchTerm(searchTerm, pageable);
	}
	
    
    public Page<Product> findProductsByCategoryName(String categoryName, int pageNo, int offset, 
    												String sortField, String sortDir, String SearchTerm) {
    	Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() :
			Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(pageNo - 1, offset,sort);
        return productListingRepository.findByCategoryNameAndSearchTermAndEnabled(categoryName, SearchTerm, pageable);
    }
}
