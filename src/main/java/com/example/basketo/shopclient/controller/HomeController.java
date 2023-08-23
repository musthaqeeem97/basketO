package com.example.basketo.shopclient.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.basketo.shopclient.service.ProductListingServiceImpl;
import com.example.basketo.shopadmin.product.model.Category;
import com.example.basketo.shopadmin.product.model.Product;
import com.example.basketo.shopadmin.product.service.CategoryServiceImpl;
import com.example.basketo.shopadmin.product.service.ProductImageService;
import com.example.basketo.shopadmin.product.service.ProductServiceImpl;
import com.example.basketo.shopadmin.user.service.AddressService;
import com.example.basketo.shopadmin.user.service.UserInfoService;
import com.example.basketo.shopadmin.user.service.UsernameProviderService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping
@RequiredArgsConstructor
public class HomeController {

	@Value("${table.pageNo}")
    private int PageNo;

    @Value("${table.offset}")
    private int offset;

    @Value("${table.sortDir}")
    private String sortDir;

    @Value("${product.table.sortField}")
    private String sortField;

    private final ProductServiceImpl productService;
    
  
    
    private final CategoryServiceImpl categoryService;
    
    private final ProductListingServiceImpl productListingService;
    
    private final ProductImageService imageService;
    
    private final UsernameProviderService usernameProviderService;
    
    private final UserInfoService userInfoService;
    
    private final AddressService addressService;
    
    @GetMapping("/page/1?sortField=name&sortDir=asc&searchTerm=")
	public String home() {
		System.out.println("inside home method");
		return "page/1?category=&sortField=name&sortDir=asc&searchTerm=";
	}
    
    @GetMapping
    public String productList(Model model){
    	
    

        return getProductsPaginated(PageNo,sortField,"",sortDir,"", model);
    }
    @GetMapping("/page/{pageNo}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String getProductsPaginated(@PathVariable(value = "pageNo") int pageNo,
                                       @RequestParam("sortField") String sortField,
                                       @RequestParam("category") String CategoryName,
                                       @RequestParam("sortDir") String sortDir,
                                       @RequestParam("searchTerm") String SearchTerm,
         
                                       Model model) {
    	
    	//for adding categories to the dropdown
    	List<Category> categoriesDropdown = categoryService.findAll();
    	categoriesDropdown.stream()
        .map(Category::getName) // Extracting the name of each category
        .forEach(System.out::println);
    	model.addAttribute("categoriesDropdown",categoriesDropdown);
    	
    	//for listing product menu
    	 Page<Product> productPage; 	
    	 List<Product> listProduct;
    	
    	//for listing categories in the home page
    	if(CategoryName=="" && SearchTerm.equals("")) {
    		listProduct = productService.getAll();
//    		Page<Category> categoryPage = categoryService.findPaginated(pageNo, pageNo, sortField, sortDir, SearchTerm);
//			List<Category> listCategory = categoryPage.getContent(); 
			List<Category> listCategory = categoryService.findAll();
			model.addAttribute("listCategory", listCategory);
			model.addAttribute("newProducts", listProduct.subList(0, 5));
			model.addAttribute("popular", listProduct.subList(5, 10));
//			model.addAttribute("totalPages", categoryPage.getTotalPages());
//    	    model.addAttribute("totalItems", categoryPage.getTotalElements());
			
    	}
    	//listing all products based on search testing need to list only enabled products
    	else if (CategoryName==""&&!SearchTerm.equals("")) {
    		 productPage =	productService.findPaginated(pageNo, offset, sortField, sortDir, SearchTerm);
    		 listProduct = productPage.getContent();
    		  model.addAttribute("listProduct", listProduct);
    		  model.addAttribute("totalPages", productPage.getTotalPages());
    	      model.addAttribute("totalItems", productPage.getTotalElements());
    	      
		}
    	//listing enabled products based on categoryname and search
    	else {
    		productPage  =	 productListingService.findProductsByCategoryName(CategoryName, pageNo, offset, sortField, sortDir, SearchTerm);
    		listProduct = productPage.getContent();
    		model.addAttribute("listProduct", listProduct);
    		model.addAttribute("totalPages", productPage.getTotalPages());
    	    model.addAttribute("totalItems", productPage.getTotalElements());
		}
 
    	//UserInfo userInfo = userInfoService.findByUuid(usernameProviderService.get()).get();
    	       
        model.addAttribute("addresses", addressService.findAddressesByUser(usernameProviderService.get()));
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        model.addAttribute("searchTerm",SearchTerm);
        
        model.addAttribute("category",CategoryName);
        System.err.println("end of product pagination controller");

        return "front-end/home";

    }
    
	
	
	
	
}

     
        	
         
         
         
  
    	
    
        


      