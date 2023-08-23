package com.example.basketo.shopadmin.cart.service;

import java.util.List;
import java.util.UUID;

import com.example.basketo.shopadmin.cart.model.CartItem;
import com.example.basketo.shopadmin.cart.repository.UserCartRepository;
import org.springframework.stereotype.Service;

import com.example.basketo.shopadmin.inventory.model.ProductInventory;
import com.example.basketo.shopadmin.inventory.repository.ProductInventoryRepository;
import com.example.basketo.shopadmin.user.model.UserInfo;


import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartServiceImpl  {
	
	private final UserCartRepository cartRepository;
	private final ProductInventoryRepository inventoryRepository;

	
	
	public void delete(CartItem cartItem) {
		cartRepository.delete(cartItem);
	}
	

	public void deleteCartItem(CartItem cartItem) {
	    cartRepository.deleteByProduct(cartItem);
	}

	public int addToCart(CartItem cartItem) {
		
	    ProductInventory productInventory = inventoryRepository.findByProduct(cartItem.getProduct());
	    
	    System.out.println("inside add to add to cart service");
	    System.err.println("available quantity: "+ productInventory.getAvailableQuantity());
	    System.err.println("quantity: "+ cartItem.getQuantity());
	   
	    if (productInventory.getAvailableQuantity()>=cartItem.getQuantity()) {
	    	
	    	 System.out.println("available quantity in inventory greater than needed quantity ");
	    	 CartItem existingCartItem = cartRepository.findByUserInfoAndProductId(cartItem);
	    	
	    	 System.out.println("existing item: "+existingCartItem);
	    	 if (existingCartItem==null) {
	    		
	    		 System.err.println(" no existing quantity int the cart: ");
				cartRepository.save(cartItem);
				return 0;
	    	 }
	    	 else if(productInventory.getAvailableQuantity()>=cartItem.getQuantity()+existingCartItem.getQuantity()) {
	    		 
	    		 System.err.println("existing quantity int the cart: "+existingCartItem.getQuantity());
		    	 
		    	 
		    	 int existingQuantity = existingCartItem.getQuantity();
		    	 
		    	 existingCartItem.setQuantity(existingQuantity+cartItem.getQuantity());
		    	 cartRepository.save(existingCartItem);
		    	 return 0;
	    	 }
	    	 
	    	 
		}
	    
	    return productInventory.getAvailableQuantity();
	}

	public List<CartItem> findCartByCustomer(UserInfo customer) {
	    List<CartItem> cartItems = cartRepository.findByUserInfo(customer);
	    return cartItems;
	}

	public CartItem findCartById(UUID cartItemId) {
		 return cartRepository.findById(cartItemId).get();
	}
	
	public boolean updateQuantity(CartItem cartItem) {
		 ProductInventory productInventory = inventoryRepository.findByProduct(cartItem.getProduct());
		  if (productInventory.getAvailableQuantity()>=cartItem.getQuantity()) {
		 cartRepository.save(cartItem);
		 return true;
		  }
		  return false;
	}

	


    
}
