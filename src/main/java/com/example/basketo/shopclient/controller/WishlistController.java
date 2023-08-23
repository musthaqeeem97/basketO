package com.example.basketo.shopclient.controller;

import com.example.basketo.shopadmin.product.model.Product;
import com.example.basketo.shopadmin.product.service.ProductService;
import com.example.basketo.shopadmin.user.model.UserInfo;
import com.example.basketo.shopadmin.user.service.AddressService;
import com.example.basketo.shopadmin.user.service.UserInfoService;
import com.example.basketo.shopadmin.user.service.UsernameProviderService;
import com.example.basketo.shopclient.model.WishlistItem;
import com.example.basketo.shopclient.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@RequestMapping("/wishlist")
public class WishlistController {

	
    	private static  int pageNo = 1;
    	private static  int offset = 10;
    	private static String sortDir = "asc";
    	private static String sortField="created_at";

	 private final ProductService productService;
	 private final UserInfoService userInfoService;
	 private final UsernameProviderService usernameProviderService;
	 private final WishlistService wishlistService;
	 private final AddressService addressService;
	 

	    @GetMapping
	    public String orderList(Model model) {
	        return getOrderPaginated(pageNo, sortField, sortDir, "", model);
	    }

	    @GetMapping("/page/{pageNo}")
	    public String getOrderPaginated(@PathVariable(value = "pageNo") int pageNo,
	                                       @RequestParam("sortField") String sortField,
	                                       @RequestParam("sortDir") String sortDir,
	                                       @RequestParam("searchTerm") String searchTerm,
	                                       Model model) {

	    
	        Page<WishlistItem> page = wishlistService.findPaginated(pageNo, offset, sortField, sortDir, searchTerm);
	        List<WishlistItem> listWishlist =   page.getContent();
	        System.out.println(page.getContent());
	     
	        model.addAttribute("addresses", addressService.findAddressesByUser(usernameProviderService.get()));
	        
	        model.addAttribute("currentPage", pageNo);
	        model.addAttribute("totalPages", page.getTotalPages());
	        model.addAttribute("totalItems", page.getTotalElements());
	        model.addAttribute("sortField", sortField);
	        model.addAttribute("sortDir", sortDir);
	        model.addAttribute("searchTerm", searchTerm);
	        model.addAttribute("listWishlist", listWishlist);
	        model.addAttribute("currentPage", pageNo);

	        return "front-end/wishlist";
	    }
	 
	 	@GetMapping("/product")
	    public ResponseEntity<String> addToWishList(@RequestParam UUID productUuid ) {
	        // Add your logic here to process the cart addition
	        // and return a success or error response
		 
		 	Product product = productService.getProductById(productUuid).get();
			System.err.println("id: "+productUuid);
						   

			    UserInfo user = userInfoService.getUser(usernameProviderService.get());
			  
			    System.out.println("product name: "+product.getName());
			   
			    boolean result = wishlistService.addToWishlist(user, product);
		 
			    if (!result) {
		        	System.out.println("The product has already been added to your wishlist.");
		            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("The product has already been added to your wishlist.");
		        } 
		            System.out.println("added to wishlist.");
		            return ResponseEntity.ok("Item added to wishlist successfully!");
		    }    
	 	
	 	@GetMapping("/delete")
	    public ResponseEntity<String> deleteCartItem(@RequestParam UUID itemId) {
	      
			wishlistService.deleteItem(wishlistService.findById(itemId));
			
	        return ResponseEntity.ok("Cart item deleted successfully");
	    }
		
	        
}
