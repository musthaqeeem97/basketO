package com.example.basketo.shopadmin.inventory.service;



import java.util.UUID;

import javax.transaction.Transactional;

import com.example.basketo.shopadmin.inventory.repository.ProductInventoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


import com.example.basketo.shopadmin.inventory.model.ProductInventory;
import com.example.basketo.shopadmin.product.model.Product;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class InventoryService  {
	
	
	
	/*------------------------------------------------------------------------------------------------------------------------*/
	
	private final ProductInventoryRepository productInventoryRepository;

	public Page<ProductInventory> findPaginated(int pageNo, int offset, String sortField, String sortDir, String searchTerm) {

    	Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() :
			Sort.by(sortField).descending();
		
	
		Pageable pageable = PageRequest.of(pageNo - 1, offset, sort);
		
		System.err.println(" end of  inventory pagination service");
		return productInventoryRepository.findAll(searchTerm,pageable);
	}
	
	public boolean addNewInventory(ProductInventory inventory) {
		
		
		ProductInventory existingInventory = productInventoryRepository.findByProduct(inventory.getProduct());
		
		System.out.println( "***********"+existingInventory);
		
		if(existingInventory!=null) {	
			System.err.println("inventory of the product id already exist.");
			return false;
		}
		
		productInventoryRepository.save(inventory);
		System.err.println("inventory added");
		return true;
	}

	  @Transactional // Ensure this service method is within a transaction
	    public void updateInventory(Product product, int quantity) {
	      
	        ProductInventory existingInventory = productInventoryRepository.findByProduct(product);

	        if (existingInventory != null) {
	            int currentQuantity = existingInventory.getAvailableQuantity();
	            existingInventory.setAvailableQuantity(currentQuantity + quantity);

	            System.out.println("Existing Quantity: " + currentQuantity);
	            System.out.println("Quantity to Add: " + quantity);
	            System.out.println("New Quantity: " + existingInventory.getAvailableQuantity());

	            productInventoryRepository.save(existingInventory);
	            System.err.println("Inventory updated");
	        } else {
	            System.err.println("Existing inventory not found");
	        }
	    }
	public ProductInventory findbyId(UUID inventoryId) {
		// TODO Auto-generated method stub
		return productInventoryRepository.findById(inventoryId).get();
	}

	public ProductInventory findbyProduct(Product product) {
		// TODO Auto-generated method stub
		return productInventoryRepository.findByProduct(product);
	}

	public void changeQuantity(Product product, int quantity) {
		// TODO Auto-generated method stub
		
		ProductInventory existingInventory = productInventoryRepository.findByProduct(product);
		existingInventory.setAvailableQuantity(existingInventory.getAvailableQuantity()-quantity);
		productInventoryRepository.save(existingInventory);
	}

	public void deleteById(UUID inventoryId) {
		productInventoryRepository.deleteById(inventoryId);
		System.out.println("inventory deleted successfully.");
		
		
	}


}
