package com.example.basketo.shopclient.controller;

import com.example.basketo.shopadmin.order.model.OrderHistory;
import com.example.basketo.shopadmin.order.model.OrderItem;
import com.example.basketo.shopadmin.order.service.OrderHistoryService;
import com.example.basketo.shopadmin.order.service.OrderService;
import com.example.basketo.shopadmin.product.model.Product;
import com.example.basketo.shopadmin.product.model.Review;
import com.example.basketo.shopadmin.product.repository.ReviewRepository;
import com.example.basketo.shopadmin.product.service.ProductServiceImpl;
import com.example.basketo.shopadmin.user.model.UserInfo;
import com.example.basketo.shopadmin.user.service.AddressService;
import com.example.basketo.shopadmin.user.service.UserInfoService;
import com.example.basketo.shopadmin.user.service.UsernameProviderService;

import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class OrderListingController {
	@Value("${table.pageNo}")
    private int pageNo;

    @Value("${order.table.offset}")
    private int offset;

    @Value("${table.sortDir}")
    private String sortDir;

    @Value("${order.table.sortField}")
    private String sortField;

  
    private final ProductServiceImpl productService;
    private final UserInfoService userInfoService;
    private final UsernameProviderService usernameProvider;
    private final OrderService orderService;
    private final OrderHistoryService orderHistoryService;
    private final AddressService addressService;
	private final ReviewRepository reviewRepository;
    
	@GetMapping("/confirm")
	public String processPayment() {
     
    	
        return "front-end/order-confirmation";
    }
    
	
	


    @GetMapping("/orders")
    public String orderList(Model model) {
        return getOrderPaginated(pageNo, sortField, sortDir, "", model);
    }

    @GetMapping("/orders/page/{pageNo}")
    public String getOrderPaginated(@PathVariable(value = "pageNo") int pageNo,
                                       @RequestParam("sortField") String sortField,
                                       @RequestParam("sortDir") String sortDir,
                                       @RequestParam("searchTerm") String searchTerm,
                                       Model model) {

    	UserInfo customer = userInfoService.findByUsername(getCurrentUsername()).get();
        Page<OrderItem> page = orderService.findPaginatedByUser(pageNo, offset, sortField, sortDir, searchTerm);
        List<OrderItem> listOrder =   page.getContent().stream().filter(item->item.getUserInfo()==customer)
        						.collect(Collectors.toList());
        
        System.out.println(listOrder.toString());
        model.addAttribute("addresses", addressService.findAddressesByUser(usernameProvider.get()));
        
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        model.addAttribute("searchTerm", searchTerm);
        model.addAttribute("listOrder", listOrder);
       
        model.addAttribute("currentPage", pageNo);

        return "front-end/orders&return";
    }
    @GetMapping("/orders/requestcancel/{id}")
    public String requestOrderCancellation(@PathVariable UUID id, @RequestParam int pageNo,Model model){
      
    	System.out.println("inside request cancel");
        OrderItem order = orderService.findById(id);
        order.setRequestCancel(true);
        orderService.save(order);
      System.out.println("request cancel: "+order.isRequestCancel());
      return "redirect:/orders/page/" + pageNo + "?sortField=" + sortField + "&sortDir=" + sortDir + "&searchTerm=";

    }
    @GetMapping("/orders/requestreturn/{id}")
    public String requestOrderReturn(@PathVariable UUID id, @RequestParam int pageNo,Model model){
      
    	System.out.println("inside request cancel");
        OrderItem order = orderService.findById(id);
        order.setRequestReturn(true);
        orderService.save(order);
      System.out.println("request cancel: "+order.isRequestReturn());
        return "redirect:/orders/page/"+pageNo;
    }
	
	
	
	  public String getCurrentUsername() {
	        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	        return authentication.getName();
	    }
	    
	  @GetMapping("/orderHistory")
	    public String orderHistoryList(Model model) {
	        return getOrderHistoryPaginated(pageNo, sortField, sortDir, "", model);
	    }

	    @GetMapping("/orderHistory/page/{pageNo}")
	    public String getOrderHistoryPaginated(@PathVariable(value = "pageNo") int pageNo,
	                                       @RequestParam("sortField") String sortField,
	                                       @RequestParam("sortDir") String sortDir,
	                                       @RequestParam("searchTerm") String searchTerm,
	                                       Model model) {

	    	UserInfo customer = userInfoService.findByUsername(getCurrentUsername()).get();
	        Page<OrderHistory> page = orderHistoryService.findPaginated(pageNo, offset, sortField, sortDir);
	        List<OrderHistory> listOrderHistory =   page.getContent().stream().filter(item->item.getUserInfo()==customer)
	        						.collect(Collectors.toList());
	        
	        System.out.println(listOrderHistory.toString());

	        model.addAttribute("currentPage", pageNo);
	        model.addAttribute("totalPages", page.getTotalPages());
	        model.addAttribute("totalItems", page.getTotalElements());
	        model.addAttribute("sortField", sortField);
	        model.addAttribute("sortDir", sortDir);
	        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
	        model.addAttribute("searchTerm", searchTerm);
	        model.addAttribute("listOrderHistory", listOrderHistory);
	        model.addAttribute("currentPage", pageNo);

	        return "front-end/orderhistory";
	    }
	    @GetMapping("/generateInvoice")
	    public String generateInvoice(@RequestParam(name = "uuid") UUID uuid){
	        orderHistoryService.generateInvoice(uuid);

	        return "redirect:/orderHistory" ;
	    }

 	    @PostMapping("/viewInvoice")
	    @ResponseBody
	    public ResponseEntity<ByteArrayResource> viewInvoice(@RequestBody String uuid) throws IOException {
	        String rootPath = System.getProperty("user.dir");
	        String uploadDir = rootPath + "/src/main/resources/static/uploads/invoices/";

	        String requestedFileName = uuid + ".pdf";
	        File requestedFile = new File(uploadDir+requestedFileName);
	        System.out.println("Searching for " + requestedFileName);

	        File directory = new File(uploadDir);
	        boolean found = false;

	        // Check if the directory exists
	        if (directory.exists() && directory.isDirectory()) {
	            // Get the list of files in the directory
	            File[] files = directory.listFiles();
	            // Iterate over the files
	            for (File file : files) {
	                if (file.isFile()) {
	                    // Get the file name
	                    String fileName = file.getName();
	                    if (fileName.equals(requestedFileName)) {
	                        requestedFile = file;
	                       found = true;
	                       break;
	                    }
	                }
	            }
	        }


	        if(found){
	            System.out.println(requestedFileName + " found");
	        }else{
	            System.out.println(requestedFileName+"+ not Found. Generating...");
	            orderHistoryService.generateInvoice(UUID.fromString(uuid));
	        }

	        System.out.println(requestedFile+"+ requested file...");
	        ByteArrayResource resource = new ByteArrayResource(FileUtils.readFileToByteArray(requestedFile));

	        // Set the content type as application/pdf
	        HttpHeaders headers = new HttpHeaders();
	        headers.setContentType(MediaType.APPLICATION_PDF);

	        // Set the file name for download
	        headers.setContentDispositionFormData("attachment", requestedFileName);

	        // Return the resource as a response with OK status
	        return ResponseEntity.ok()
	                .headers(headers)
	                .body(resource);
	    }
 	    
 	   @GetMapping("/productreview")
 	    public String showAddReviewForm(@RequestParam UUID productId, Model model) {
 		   
 		  Product product = productService.findById(productId).orElseThrow();
 		  Review existingReview = reviewRepository.findByUserInfoAndProduct(userInfoService.findByUuid(usernameProvider.get()).get(),product);
 		System.out.println("existing review: "+existingReview);
 		  if (existingReview!=null) {
 			 System.out.println("existing review: "+existingReview.getComment());
 			reviewRepository.delete(existingReview);
 		}
 	        model.addAttribute("product", product);
 	        model.addAttribute("review", new Review());
 	        model.addAttribute("reviews", product.getReviews());
 	            product.getReviews();
 	        return "front-end/product-review";
 	        
 	        
 	    }

 	    @PostMapping("/add-review")
 	    public String addReview(@RequestParam UUID productId,
 	                            @RequestParam String comment , @RequestParam String rating) {
 	    	
 	    	System.out.println("inside add review "+ rating);
 	    	System.out.println("comment:"+comment);
 	    	
 	    	int parsedRating;

 	    	switch (rating) {
 	    	    case "1":
 	    	        parsedRating = 1;
 	    	        break;
 	    	    case "2":
 	    	        parsedRating = 2;
 	    	        break;
 	    	    case "3":
 	    	        parsedRating = 3;
 	    	        break;
 	    	    case "4":
 	    	        parsedRating = 4;
 	    	        break;
 	    	    case "5":
 	    	        parsedRating = 5;
 	    	        break;
 	    	    default:
 	    	        // Handle the case where the rating is not within the expected range.
 	    	        // You can set a default value or show an error message.
 	    	        parsedRating = 1; // For example, setting a default rating of 0.
 	    	        break;
 	    	}

 	    	
 	    	
 	        Product product = productService.findById(productId).orElseThrow();
 	        UserInfo user = userInfoService.findByUuid(usernameProvider.get()).get();
 	        Review review = Review.builder().userInfo(user)
 	        				.product(product)
 	        				.comment(comment)
 	        				.rating(parsedRating)
 	        				.build();
 	        reviewRepository.save(review); 	       
 	       // Redirect to product details page
 	        return "redirect:/orders";
 	    }
 	   
 	    

}
