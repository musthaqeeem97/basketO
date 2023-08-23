package com.example.basketo.shopclient.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.basketo.shopadmin.cart.model.CartItem;
import com.example.basketo.shopadmin.cart.service.CartServiceImpl;
import com.example.basketo.shopadmin.inventory.model.ProductInventory;
import com.example.basketo.shopadmin.inventory.service.InventoryService;
import com.example.basketo.shopadmin.product.model.Product;
import com.example.basketo.shopadmin.product.service.ProductServiceImpl;
import com.example.basketo.shopadmin.user.model.UserInfo;
import com.example.basketo.shopadmin.user.service.UserInfoService;
import com.example.basketo.shopadmin.user.service.UsernameProviderService;


import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartListController {

	

	private final CartServiceImpl cartService;
	private final ProductServiceImpl productService;
	private final UserInfoService userInfoService;
	private final UsernameProviderService usernameProviderService;
	private final InventoryService inventoryService;
	
	 @GetMapping("/product")
	    public ResponseEntity<String> addToCart(@RequestParam UUID productUuid, @RequestParam int quantity ) {
	        // Add your logic here to process the cart addition
	        // and return a success or error response
		 
		 	Product product = productService.getProductById(productUuid).get();
			System.err.println("quantity: "+quantity);
			
				ProductInventory inventory= inventoryService.findbyProduct(product);
		
			    if (inventory==null) {
					return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error adding to cart.There is no inventory for the product"+ product.getName());
				}


			    UserInfo user = userInfoService.getUser(usernameProviderService.get());
			    System.err.println(quantity + "****************************");
			    System.out.println("product name: "+product.getName());
			    
			    CartItem cartItem = CartItem.builder()
			            .userInfo(user)
			            .product(product)
			            .quantity(quantity)
			            .build();

			    int result = cartService.addToCart(cartItem);
		 
	        if (result>0) {
	        	System.out.println("quantity not available");
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error adding to cart.");
	        } else {
	        	
	            System.out.println("added to cart");
	            return ResponseEntity.ok("Item added to cart successfully!");
	        }
	    }
	


	@GetMapping
	public String showCart(Model model) {
	    UserInfo customer = userInfoService.getUser(usernameProviderService.get());
	    List<CartItem> listCartItems = cartService.findCartByCustomer(customer);
	    
	    if (!listCartItems.isEmpty()) {
	    	 System.out.println("cart 0: "+listCartItems.get(0));
	 	    String  imageName = listCartItems.get(0).getProduct().getMainImage().getFileName();
	 	  
	 	    System.err.println("image name: "+ imageName);
	 	    
	 	    //wrong logic need to change
	 	    int totalItems=listCartItems.size();
	 	    
	 	    Double totalPrice = listCartItems.stream()
	 	            .mapToDouble(cartItem -> cartItem.getProduct().getPrice() * cartItem.getQuantity())
	 	            .sum();
	 	  
	 	   System.out.println("total items: "+totalItems  );
	 	   System.out.println("total price: "+totalPrice  );
	 	  model.addAttribute("totalItems", totalItems);
		    model.addAttribute("totalPrice", totalPrice);
		}
	   
	    
	    model.addAttribute("listCart",listCartItems);
	   
	    return "front-end/cart";
	}
	@GetMapping("/delete")
    public ResponseEntity<String> deleteCartItem(@RequestParam UUID cartItemId) {
      
		cartService.delete(cartService.findCartById(cartItemId));
		
        return ResponseEntity.ok("Cart item deleted successfully");
    }
	


	@GetMapping("/update")
    public ResponseEntity<String> updateCartItem(@RequestParam UUID cartItemId, @RequestParam int quantity) {
        System.out.println("quantity: "+quantity);
        CartItem cartItem = cartService.findCartById(cartItemId);
        cartItem.setQuantity(quantity);
       if (cartService.updateQuantity(cartItem)==true) {
    	   return ResponseEntity.ok("Quantity updated successfully");
       }
       return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("required qunatity not available");
    }
	


}
