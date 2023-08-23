package com.example.basketo.shopadmin.inventory.controller;

import java.util.List;
import java.util.UUID;

import com.example.basketo.shopadmin.inventory.model.ProductInventory;
import com.example.basketo.shopadmin.inventory.service.InventoryService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.basketo.shopadmin.product.model.Product;
import com.example.basketo.shopadmin.product.service.ProductServiceImpl;


import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/adminpanel/inventory")
@RequiredArgsConstructor
public class InventoryController {

	@Value("${table.pageNo}")
    private int pageNo;

    @Value("${table.offset}")
    private int offset;

    @Value("${table.sortDir}")
    private String sortDir;

    @Value("${inventory.table.sortField}")
    private String sortField;

  
    private final InventoryService inventoryService;
    private final ProductServiceImpl productServiceImpl;
   // private final WarehouseRepository warehouseRepository;

    

    @GetMapping
    public String inventoryList(Model model) {
        return getInventoryPaginated(pageNo, sortField, sortDir, "", model);
    }

    @GetMapping("/page/{pageNo}")
    public String getInventoryPaginated(@PathVariable(value = "pageNo") int pageNo,
                                       @RequestParam("sortField") String sortField,
                                       @RequestParam("sortDir") String sortDir,
                                       @RequestParam("searchTerm") String searchTerm,
                                       Model model) {
       
    	Page<ProductInventory> page = inventoryService.findPaginated(pageNo, offset, sortField, sortDir, searchTerm);
    	List<ProductInventory> listInventory = page.getContent();

        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        model.addAttribute("searchTerm", searchTerm);
        model.addAttribute("listInventory", listInventory);

        return "inventory/adminpanel-inventory";
    }

    @GetMapping("/create/{productId}")
    public String getAddInventoryPage(@PathVariable("productId") UUID productId, Model model) {
        
    	Product product = productServiceImpl.findById(productId).get();
    	
        model.addAttribute("product", product);

        model.addAttribute("inventory", new ProductInventory());
        return "inventory/add-inventory";
    }

    @PostMapping("/create")
    public String addInventory(@ModelAttribute("productId") UUID productId,@ModelAttribute("availableQuantity") int quantity, Model model ) {
        //System.err.println("Inside addInventory method: " + " " + " " + inventory.getAvailableQuantity());

    	Product product = productServiceImpl.getProductById(productId).get();
    	//Warehouse warehouse = warehouseRepository.findById(code).get();
    	 // Replace with the actual quantity

    	ProductInventory inventory = new ProductInventory(product,quantity);
    	 boolean result =  inventoryService.addNewInventory(inventory);
    	 
    	 if (!result) {
			model.addAttribute("productError","inventiry with the provided product id already exist");
			
			model.addAttribute("product", product);
			
			return "inventory/add-inventory";
    	    
		}

        return "redirect:/adminpanel/inventory";
    }
    
    @GetMapping("/update/{inventoryId}")
    public String getUpdateInventoryPage(@PathVariable("inventoryId") UUID inventoryId, Model model) {
        
    	ProductInventory inventory = inventoryService.findbyId(inventoryId);
    	
    
        model.addAttribute("inventory", inventory);
        return "inventory/update-inventory";
    }
    
    @PostMapping("/update")
    public String updateInventory(@ModelAttribute("productId") UUID productId,@ModelAttribute("availableQuantity") int quantity, Model model) {
        
    	System.out.println("product id: " + productId);
    	System.out.println("quantity : " + quantity);
    	
    	Product product = productServiceImpl.findById(productId).get();
       	inventoryService.updateInventory(product,quantity);
       
       
        return "redirect:/adminpanel/inventory";
    }
    
    @GetMapping("/delete/{inventoryId}")
    public String deleteInventory(@PathVariable("inventoryId") UUID inventoryId) {
    	inventoryService.deleteById(inventoryId);
		
        return "redirect:/adminpanel/inventory"; // For example, redirect to the inventory list page
    }
    
   
   

}