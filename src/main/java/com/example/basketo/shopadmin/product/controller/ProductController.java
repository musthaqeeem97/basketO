package com.example.basketo.shopadmin.product.controller;

import com.example.basketo.shopadmin.product.service.CategoryServiceImpl;
import com.example.basketo.shopadmin.product.service.ProductImageServiceImpl;
import com.example.basketo.shopadmin.product.service.ProductServiceImpl;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.basketo.shopadmin.product.model.Category;
import com.example.basketo.shopadmin.product.model.ProductImage;
import com.example.basketo.shopadmin.product.model.Thumbnail;
import com.example.basketo.shopadmin.product.model.Product;
import com.example.basketo.shopadmin.product.service.ThumbnailServiceImpl;
import com.example.basketo.shopadmin.user.service.UsernameProviderService;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;

@Controller
@RequestMapping("/adminpanel/products")
@RequiredArgsConstructor
public class ProductController {

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
    
    private final ProductImageServiceImpl imageService;
    
    private final UsernameProviderService usernameProviderService;
    
    private final ThumbnailServiceImpl thumbnailService;

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String productList(Model model){

        return getProductsPaginated(PageNo, sortField, sortDir,"", model);
    }
    @GetMapping("/page/{pageNo}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String getProductsPaginated(@PathVariable(value = "pageNo") int pageNo,
                                       @RequestParam("sortField") String sortField,@RequestParam("sortDir") String sortDir,
                                       @RequestParam("searchTerm") String SearchTerm,
         
                                       Model model) {

    
        Page<Product> page = productService.findPaginated(pageNo, offset, sortField, sortDir,SearchTerm);

        List<Product> listProduct= page.getContent();

        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());

        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        model.addAttribute("searchTerm",SearchTerm);

        model.addAttribute("listProduct", listProduct);
        

        System.out.println("***************");
        System.out.println("***************");

        System.err.println("end of product pagination controller");

        return "product/adminpanel-products";

    }
    
    @GetMapping("/create")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String getAddProduct(Model model) throws JsonProcessingException {
  
        Product product = new Product();
        List<Category> categories = categoryService.findAll();
        model.addAttribute("categories", categories);
        model.addAttribute("product", product);


        return "product/add-product";
    }
    

    @PostMapping("/create")
       @PreAuthorize("hasRole('ROLE_ADMIN')")
       public String saveProduct(@ModelAttribute Product product,
                                 BindingResult result,
                                 @RequestParam("mainImage") MultipartFile image,
                                 @RequestParam("thumbnails") List<MultipartFile> thumbnails,
                                 Model model) throws IOException {


     	  System.err.println("inside  create product controller");


     	 Optional<Product>  existingProduct =   productService.findByName(product.getName());
     	 if (existingProduct.isPresent()) {
     		 List<Category> categories = categoryService.findAll();
            model.addAttribute("categories", categories);
              System.err.println("product name already exists");
              model.addAttribute("nameerror","product name already exists.");
              
             
              return "product/add-product";
          }
     	 
     	 //save the product 
           product = productService.save(product);

          
           if(!image.getOriginalFilename().equals("")){
             
                   String fileLocation = handleFileUpload(image); // Save the image and get its file location
                   ProductImage imageEntity = new ProductImage(fileLocation,product); // Create an Image entity with the file location
                   imageEntity = imageService.saveImage(imageEntity);
                   // Add the Image entity to the Product's list of images
                  imageService.saveImage(imageEntity);
           }
           if(thumbnails!=null && !thumbnails.get(0).getOriginalFilename().equals("")){
               
           	for (MultipartFile thumbnail : thumbnails) {
           		String fileLocation = handleFileUpload(thumbnail); // Save the image and get its file location
           			Thumbnail imageEntity = new Thumbnail(fileLocation,product); // Create an Image entity with the file location
                   imageEntity = thumbnailService.save(imageEntity);
                // Add the Image entity to the Product's list of image
                
           		}          
            
           }
        
       
        return "redirect:/adminpanel/products";
    }
    
    
    @GetMapping("/edit/{id}")
    public String updateCategory(@PathVariable UUID id, RedirectAttributes attributes, Model model) {
        Optional<Product> optionalProduct = productService.findById(id);
        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();      
            model.addAttribute("product", product);
            List<Category> categories = categoryService.findAll();
            model.addAttribute("categories", categories);
            return "product/update-product";
        } else {
            attributes.addFlashAttribute("error", "Product not found.");
            return "redirect:/adminpanel/products";
        }
    }

    @PostMapping("/update")
    public String updateProduct(@ModelAttribute("product") Product product,
    		BindingResult result, @RequestParam("mainImage") MultipartFile image,
            @RequestParam("thumbnails") List<MultipartFile> thumbnails,
            Model model) throws IOException {
    	
        boolean updated = productService.update(product);
        if (!updated ) {
           // model.addAttribute("product", product);
            List<Category> categories = categoryService.findAll();
            model.addAttribute("categories", categories);
            model.addAttribute("nameError", "Product name already exists.");
            return "product/update-product";
        }
  
        	
        	System.err.println(product.getUuid());
        	 String deleteDir;
        	 String filePath;
        	 
			//deleting main image form the filepath
			if (product.getMainImage()!=null) {
				ProductImage existingImage= product.getMainImage();
	        	
	        	        deleteDir = System.getProperty("user.dir")+ "/src/main/resources/static/images/product";
	        	        // Get the file path
	        	        filePath = deleteDir + "/" + existingImage.getFileName();
	        	        handleFileDelete(filePath);
	        	        
				}
			
			if (product.getThumbnails()!=null) {
				 List<Thumbnail> existingThumbnails= product.getThumbnails();
				for (Thumbnail thumbnail: existingThumbnails) {
		        	
        	        deleteDir = System.getProperty("user.dir")+ "/src/main/resources/static/images/product";
        	        // Get the file path
        	        filePath = deleteDir + "/" + thumbnail.getFileName();
        	        handleFileDelete(filePath);
        	        }
				}
				      	
			
        	  
			//deleting all images both main and thumbnails of the updated product form the image table
         	imageService.deleteImageByProduct(product);
         	
         	thumbnailService.deleteThumbnailByProduct(product);
         	
        	
        	
            // Process file uploads
         	
         	//uploading main image
         	 if (!image.getOriginalFilename().equals("")) {
             	       	     
                 //String filePath = handleFileUpload(image,uploadDir); // Save the image and get its file location
             String fileName =   handleFileUpload(image);
                 ProductImage imageEntity = ProductImage.builder()
                         .product(product)
                         .fileName(fileName)
                         .build(); // Create an Image entity with the cropped file location

                 imageEntity = imageService.saveImage(imageEntity);
                 
                 //save image to the table
                 product.setMainImage(imageEntity);
                
             } else {
                 throw new IllegalArgumentException("Invalid file type. Only image files are allowed.");
             }
         	
         	
         	
         	//to collect the new thumbnails
            List<Thumbnail> thumbnailList = new ArrayList<>();
           
            //uploading sub images
            if ( thumbnails!=null && !thumbnails.get(0).getOriginalFilename().equals("")) {
	       	     for (MultipartFile thumbnail : thumbnails) {
					
			
                //String filePath = handleFileUpload(image,uploadDir); // Save the image and get its file location
            String fileName =   handleFileUpload(thumbnail);
                Thumbnail imageEntity = Thumbnail.builder()
                        .product(product)
                        .fileName(fileName)
                        .build(); // Create an Image entity with the cropped file location
       
                //saving to thumbnails table
                imageEntity = thumbnailService.save(imageEntity);
                      
                //adding thumbnails to a list to latter set to thumbnail List of the product
                thumbnailList.add(imageEntity);
	       	}
	       	 
            } else {
                throw new IllegalArgumentException("Invalid file type. Only image files are allowed.");
            }
        	
            product.setThumbnails(thumbnailList);
            
           //saving the product
            productService.save(product);

        return "redirect:/adminpanel/products";
    }
    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable UUID id, RedirectAttributes attributes, Model model) throws IOException {
        
     Product existingProduct = productService.findById(id).get();
     
     
     //deleteing file from file paths 
   	 String deleteDir;
   	 String filePath;
   	 
   	 //deleting main image from the path
   	 if (existingProduct.getMainImage()!=null) {
    		ProductImage existingImage= existingProduct.getMainImage();
       	
       	        deleteDir = System.getProperty("user.dir")+ "/src/main/resources/static/images/products";
       	        // Get the file path
       	        filePath = deleteDir + "/" + existingImage.getFileName();
       	        handleFileDelete(filePath);
       	        
    		}
   	 
   	 
   	 //deleting thumbnails from the path
   	 if (existingProduct.getThumbnails()!=null) {
   		List<Thumbnail> existingThumbnails= existingProduct.getThumbnails();
      	 for (Thumbnail thumbnail : existingThumbnails) {
      		
      	        deleteDir = System.getProperty("user.dir")+ "/src/main/resources/static/images/products";
      	        // Get the file path
      	        filePath = deleteDir + "/" + thumbnail.getFileName();
      	        handleFileDelete(filePath);
      	        
   		}
      	 
  
      	
	}
   	 
   	 	//deleting main image 
	 	imageService.deleteImageByProduct(existingProduct);	
	 	
	 	//deleteing thumbnails
	 	
	 	thumbnailService.deleteThumbnailByProduct(existingProduct);
        
	 	//deleting product
	   	productService.deleteById(id);
	   	
	    attributes.addFlashAttribute("deleteMessage", "Product deleted successfully");
        
        
        
        return "redirect:/adminpanel/products";
    }

//    //to specifically add more images
//    @GetMapping("/add-image/{id}")
//    public String addImages(@PathVariable UUID id, Model model ){
//        
//     Product existingProduct = productService.findById(id).get();
//     model.addAttribute("product", existingProduct);
//     
//        return "product/update-productimages";
//    }
//    @PostMapping("/update-image")
//    public String updateImages(@ModelAttribute("product") Product product,
//    		BindingResult result, @RequestParam("images") List<MultipartFile> imageFiles,
//            Model model) throws IOException {
//    	
//       
//  
//        	
//        	System.err.println(product.getUuid());
//  
//			Product existingProduct =   productService.findById(product.getUuid()).get();
//			
//			
//        	 
//            
//			//getting existing products image list
//            List<ProductImage> images = existingProduct.getImages();
//            
//            // Process file uploads
//            for (MultipartFile image : imageFiles) {
//                if (image.getContentType().startsWith("image/")) {
//                	
//     
//                    //String filePath = handleFileUpload(image,uploadDir); // Save the image and get its file location
//                String fileName =   handleFileUpload(image);
//                   
//
//                    ProductImage imageEntity = ProductImage.builder()
//                            .product(product)
//                            .fileName(fileName)
//                            .build(); // Create an Image entity with the cropped file location
//
//                    imageEntity = imageService.saveImage(imageEntity);
//                    
//                    //adding new images
//                    images.add(imageEntity);
//                } else {
//                    throw new IllegalArgumentException("Invalid file type. Only image files are allowed.");
//                }
//            }
//
//            product.setImages(images);
//            productService.save(existingProduct);
//
//        return "redirect:/adminpanel/products";
//    }
    
    @GetMapping("/active/{id}")
    public String toggleUserStatus(@PathVariable("id") UUID id) {
    	
       Product product = productService.findById(id).get();
        
      
            
          //if enabled == true will change it to false if false will change it true
            Boolean status = !product.isEnabled();
            product.setEnabled(status);
            
            if (product.isEnabled()==false) {
            	product.setDeleted(true);
            	product.setDeletedAt(new Date());
            	product.setDeletedBy(usernameProviderService.get());
			}else {
				product.setDeleted(false);
				product.setCreatedAt(new Date());
			}
            productService.save(product);
        
        return "redirect:/adminpanel/products";
    }

    
//    @GetMapping("/delete/{uuid}")
//    @PreAuthorize("hasRole('ROLE_ADMIN')")
//    public String delete(@PathVariable UUID uuid){
//
//        Product product = productService.getProductById(uuid).get();
//        product.setDeletedBy(usernameProviderService.get());
//        product.setDeleted(true);
//        product.setEnabled(false);
//        product.setDeletedAt(new Date());
//        
//        System.out.println("***************");
//        System.err.println("Soft deleting product "+product.getName());
//        productService.save(product);
//
//
////        Product product = productService.getProduct(uuid);
////        //delete all images of the product first
////        for(Image image : product.getImages()){
////            imageService.delete(image.getUuid());
////        }
////        productService.delete(product.getUuid());
//        return "redirect:/adminpanel/products";
//    }
    
   
    private String handleFileUpload(MultipartFile file) throws IOException {
    
        String rootPath = System.getProperty("user.dir");
        String uploadDir = rootPath + "/src/main/resources/static/images/products";

        // Create the directory if it doesn't exist
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        // Generate a unique file name for the uploaded file
        String fileName = UUID.randomUUID().toString() + "-" + file.getOriginalFilename();
        // Save the file to the upload directory
        String filePath = uploadDir + "/" + fileName;
        Path path = Paths.get(filePath);
        Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
        System.gc();

        // Return the file path
        return fileName;
    }
private void handleFileDelete(String filePath) throws IOException {
        
        // Create a file object for the file to be deleted
        File file = new File(filePath);

        // Check if the file exists
        if (file.exists()) {
            // Delete the file
            file.delete();
            System.out.println("File deleted successfully!");
        } else {
            System.out.println("File not found!");
        }
    }


}
