package com.example.basketo.shopadmin.product.controller;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.imageio.ImageIO;

import com.example.basketo.shopadmin.product.service.CategoryImageServiceImpl;
import com.example.basketo.shopadmin.product.service.CategoryServiceImpl;
import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
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
import com.example.basketo.shopadmin.product.model.CategoryImage;

import lombok.RequiredArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;

@Controller
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
@RequestMapping("/adminpanel/category")
@RequiredArgsConstructor
public class CategoryController {

    @Value("${table.pageNo}")
    private int pageNo;

    @Value("${table.offset}")
    private int offset;

    @Value("${table.sortDir}")
    private String sortDir;

    @Value("${category.table.sortField}")
    private String sortField;

    private final CategoryServiceImpl categoryService;
    private final CategoryImageServiceImpl categoryImageService;

    

    @GetMapping
    public String categoryList(Model model) {
        return getCategoryPaginated(pageNo, sortField, sortDir, "", model);
    }

    @GetMapping("/page/{pageNo}")
    public String getCategoryPaginated(@PathVariable(value = "pageNo") int pageNo,
                                       @RequestParam("sortField") String sortField,
                                       @RequestParam("sortDir") String sortDir,
                                       @RequestParam("searchTerm") String searchTerm,
                                       Model model) {
        Page<Category> page = categoryService.findPaginated(pageNo, offset, sortField, sortDir, searchTerm);
        List<Category> listCategory = page.getContent();

        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        model.addAttribute("searchTerm", searchTerm);
        model.addAttribute("listCategory", listCategory);

        return "product/adminpanel-category";
    }

    @GetMapping("/create")
    public String addCategory(Model model) {
        model.addAttribute("category", new Category());
        return "product/add-category";
    }

    @PostMapping("/create")
    public String addCategory(@ModelAttribute("category") Category category,
                              BindingResult result,
                              @RequestParam("images") List<MultipartFile> imageFiles,
                              Model model) throws IOException {
        String name = category.getName();
        Category existingCategory = categoryService.findByName(name);

        if (existingCategory != null) {
            model.addAttribute("nameError", "Category name already exists.");
            return "product/add-category";
        }

        categoryService.save(category);

        try {
            // Process file uploads
            List<CategoryImage> images = new ArrayList<>();
            for (MultipartFile image : imageFiles) {
                if (image.getContentType().startsWith("image/")) {
                	
                	 String fileName = category.getName() + "-" + image.getOriginalFilename();
                     String uploadDir =  System.getProperty("user.dir")+ 
                    		 "/src/main/resources/static/images/category";
                     String fileLocation = uploadDir + "/" + fileName; // Specify the file save location

                    //String filePath = handleFileUpload(image,uploadDir); // Save the image and get its file location
                    handleFileUpload(uploadDir,fileLocation,image);
                    
                    // Crop the image to a specific size using Thumbnailator
                    BufferedImage croppedImage = Thumbnails.of(fileLocation)
                            .size(300, 300)
                            .asBufferedImage();

                  //directory for saving croped-category image for the adminpanel
                    uploadDir =  System.getProperty("user.dir")+ 
                   		 "/src/main/resources/static/cropped-images/category";

                    
                    // Save the cropped image
                    saveImageFile(croppedImage, uploadDir, fileName);

                    CategoryImage imageEntity = CategoryImage.builder()
                            .category(category)
                            .fileName(fileName)
                            .build(); // Create an Image entity with the cropped file location

                    
                    imageEntity = categoryImageService.saveImage(imageEntity);
                    images.add(imageEntity);
                } else {
                    throw new IllegalArgumentException("Invalid file type. Only image files are allowed.");
                }
            }

            category.setImages(images);
            categoryService.save(category);

        } catch (FileSizeLimitExceededException ex) {
            model.addAttribute("fileError", "File size exceeds the maximum limit.");
            return "product/add-category";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("fileError", ex.getMessage());
            return "product/add-category";
        }

        return "redirect:/adminpanel/category";
    }

    @GetMapping("/edit/{id}")
    public String updateCategory(@PathVariable UUID id, RedirectAttributes attributes, Model model) {
        Optional<Category> optionalCategory = categoryService.findByUuid(id);
        if (optionalCategory.isPresent()) {
            Category category = optionalCategory.get();
            model.addAttribute("category", category);
            return "product/update-category";
        } else {
            attributes.addFlashAttribute("error", "Category not found.");
            return "redirect:/adminpanel/category";
        }
    }

    @PostMapping("/update")
    public String updateCategory(@ModelAttribute("category") Category category,
    		BindingResult result, @RequestParam("images") List<MultipartFile> imageFiles,
            Model model) throws IOException {
    	
        boolean updated = categoryService.update(category);
        if (!updated ) {
            model.addAttribute("nameError", "Category name already exists.");
            return "product/update-category";
        }
        try {
        	
        	System.err.println(category.getUuid());
        	 String deleteDir;
        	 String filePath;
        	 
				
			if (category.getImages()!=null) {
				List<CategoryImage> existing_images= category.getImages();
	        	 for (CategoryImage image : existing_images) {
	        		
	        	        deleteDir = System.getProperty("user.dir")+ "/src/main/resources/static/images/category";
	        	        // Get the file path
	        	        filePath = deleteDir + "/" + image.getFileName();
	        	        handleFileDelete(filePath);
	        	        
	        	        deleteDir = System.getProperty("user.dir")+ "/src/main/resources/static/cropped-images/category";
	        	        // Get the file path
	        	        filePath = deleteDir + "/" + image.getFileName();
	        	        handleFileDelete(filePath);
				}
	        	
			}
        	 
			//deleting all images of the updated category from table
         	categoryImageService.deleteImageByCategory(category);
         	
        	
        	
            // Process file uploads
            List<CategoryImage> images = new ArrayList<>();
            for (MultipartFile image : imageFiles) {
                if (image.getContentType().startsWith("image/")) {
                	
      
                     String fileName = category.getName() + "-" + image.getOriginalFilename();
                     String uploadDir =  System.getProperty("user.dir")+ 
                    		 "/src/main/resources/static/images/category";
                     String fileLocation = uploadDir + "/" + fileName; // Specify the file save location

                    //String filePath = handleFileUpload(image,uploadDir); // Save the image and get its file location
                    handleFileUpload(uploadDir,fileLocation,image);
                    
                    // Crop the image to a specific size using Thumbnailator
                    BufferedImage croppedImage = Thumbnails.of(fileLocation)
                            .size(300, 300)
                            .asBufferedImage();

                  //directory for saving croped-category image for the adminpanel
                    uploadDir =  System.getProperty("user.dir")+ 
                   		 "/src/main/resources/static/cropped-images/category";

                    
                    // Save the cropped image
                   saveImageFile(croppedImage, uploadDir, fileName);

                    CategoryImage imageEntity = CategoryImage.builder()
                            .category(category)
                            .fileName(fileName)
                            .build(); // Create an Image entity with the cropped file location

                    imageEntity = categoryImageService.saveImage(imageEntity);
                    images.add(imageEntity);
                } else {
                    throw new IllegalArgumentException("Invalid file type. Only image files are allowed.");
                }
            }

            category.setImages(images);
            categoryService.save(category);

        } catch (FileSizeLimitExceededException ex) {
            model.addAttribute("fileError", "File size exceeds the maximum limit.");
            return "product/add-category";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("fileError", ex.getMessage());
            return "product/add-category";
        }

        return "redirect:/adminpanel/category";
    }
    @GetMapping("/delete/{id}")
    public String deleteCategory(@PathVariable UUID id, RedirectAttributes attributes, Model model) throws IOException {
        
     Category existingCategory = categoryService.findByUuid(id).get();
     
     
     //deleteing file from file paths 
   	 String deleteDir;
   	 String filePath;
   	 
   	 if (existingCategory.getImages()!=null) {
   		List<CategoryImage> existing_images= existingCategory.getImages();
      	 for (CategoryImage image : existing_images) {
      		
      	        deleteDir = System.getProperty("user.dir")+ "/src/main/resources/static/images/category";
      	        // Get the file path
      	        filePath = deleteDir + "/" + image.getFileName();
      	        handleFileDelete(filePath);
      	        
      	        deleteDir = System.getProperty("user.dir")+ "/src/main/resources/static/cropped-images/category";
      	        // Get the file path
      	        filePath = deleteDir + "/" + image.getFileName();
      	        handleFileDelete(filePath);
   		}
  
      	
	}
   	 
   //deleting all images of the updated category from table
	 	categoryImageService.deleteImageByCategory(existingCategory);	
        
	 	//deleting category
	   	categoryService.deleteById(id);
	   	
	    attributes.addFlashAttribute("deleteMessage", "Category deleted successfully");
        
        
        
        return "redirect:/adminpanel/category";
    }

    private void handleFileUpload(String uploadDir,String fileLocation,MultipartFile file) throws IOException {
     
        // Create the directory if it doesn't exist
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // Generate a unique file name for the uploaded file
        Path path = Paths.get(fileLocation);
        Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

    }

    private void saveImageFile(BufferedImage image, String uploadDir, String fileName) throws IOException {
     
    	
        String fileLocation = uploadDir + "/" + fileName; // Specify the file save location
        
        // Create the directory if it doesn't exist, 
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }


        // Save the cropped image to the specified location
        ImageIO.write(image, "jpg", new File(fileLocation));

       
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
