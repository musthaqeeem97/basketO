package com.example.basketo.shopadmin.product.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.basketo.shopadmin.product.model.Category;

import com.example.basketo.shopadmin.product.repository.CategoryRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CategoryServiceImpl implements CategoryService{

    @Autowired
    CategoryRepository categoryRepository;
    
    
//    @PostConstruct
//    public void initDB() {
//    		List<Category> catogoryList = IntStream.rangeClosed(1, 20)
//             .mapToObj(i -> Category.builder().name("category"+i).description("testing categories").build())
//             .collect(Collectors.toList());
//     categoryRepository.saveAll(catogoryList);
//    }
    
    
    @Override
	public Page<Category> findPaginated(int pageNo, int offset, String sortField, String sortDir, String SearchTerm) {
		
    	Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() :
			Sort.by(sortField).descending();
		
	
		Pageable pageable = PageRequest.of(pageNo - 1, offset, sort);
		
		System.err.println(" end of category service");
		return categoryRepository.findAll(SearchTerm,pageable);
	}
    
//    public Page<Category> findPaginated(int pageNo, int pageSize, String sortField, String sortDirection) {
//		// TODO Auto-generated method stub
//		Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() :
//			Sort.by(sortField).descending();
//		
//	
//		Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
//		return categoryRepository.findAll(pageable);
//		
//	}


    @Override
	public Optional<Category> findByUuid(UUID id) {
		// TODO Auto-generated method stub
		return categoryRepository.findByUuid(id);
	}


	@Override
	public Category findByName(String name) {
		// TODO Auto-generated method stub
		return categoryRepository.findByName(name);
	}


	@Override
	public void save(Category category) {
		categoryRepository.save(category);
		
	}
	
	public boolean update(Category category) {
		
		Category existingCategory =  categoryRepository.findByName(category.getName());
		
		
	if(existingCategory!=null && !existingCategory.getUuid().toString().equals(category.getUuid().toString()) ) {
		System.out.println(category.getUuid());
		System.out.println(existingCategory.getUuid());
	
		System.err.println("name already exist.");
		return false;
	}
	 existingCategory = categoryRepository.findById(category.getUuid()).get();
	 existingCategory.setImages(category.getImages());
	 existingCategory.setName(category.getName());
	 existingCategory.setDescription(category.getDescription());
	 
	 categoryRepository.save(existingCategory);
       	
	 return true;

	}


	public List<Category> findAll() {
		// TODO Auto-generated method stub
		return categoryRepository.findAll();
	}

	public void deleteById(UUID id) {
		categoryRepository.deleteById(id);
		
	}


	
}