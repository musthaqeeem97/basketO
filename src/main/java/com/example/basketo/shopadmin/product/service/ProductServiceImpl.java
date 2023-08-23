package com.example.basketo.shopadmin.product.service;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.basketo.shopadmin.product.model.ProductImage;
import com.example.basketo.shopadmin.product.model.Product;
import com.example.basketo.shopadmin.product.repository.CategoryRepository;
import com.example.basketo.shopadmin.product.repository.ProductImageRepository;
import com.example.basketo.shopadmin.product.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService{
	
	private final CategoryRepository categoryRepository;

    
    private final ProductRepository productRepository;


    private final ProductImageService imageService;

    private final ProductImageRepository imageRepository;

    //for testing
//    @PostConstruct
//    public void initDB() {
//    		List<Category> catogoryList = IntStream.rangeClosed(1, 20)
//             .mapToObj(i -> Category.builder().name("category"+i).description("testing categories").build())             .collect(Collectors.toList());
//     categoryRepository.saveAll(catogoryList);
//   
//      Category category1 =   categoryRepository.findByName("category1");
//      Category category2 =   categoryRepository.findByName("category2");
//     
//     List<Product> productList = IntStream.rangeClosed(1, 5)
//             .mapToObj(i -> Product.builder().name("product"+i).price(new Random().nextFloat(2000)).description("testing products").category(category1).build())
//             .collect(Collectors.toList());
//     List<Product> productList2 = IntStream.rangeClosed(6, 10)
//             .mapToObj(i -> Product.builder().name("product"+i).price(new Random().nextFloat(2000)).description("testing products").category(category2).build())
//             .collect(Collectors.toList());  
//     productRepository.saveAll(productList);
//     productRepository.saveAll(productList2);
//     
//     List<Product> listProduct = productRepository.findAll();
//     
//     List<String> categoryNames = listProduct.stream()
//             .map(product -> product.getCategory().getName())
//             .collect(Collectors.toList());
//             
//     System.err.println("products added");
//    }

    @Override
    public Product getByName(String productName) {
        return productRepository.findByNameLike("%"+productName+"%");
    }

    @Override
    public Product save(Product product) {
         return productRepository.save(product);
    }

    @Override
    public List<Product> getAll() {
        return productRepository.findAll();
    }

    @Override
    public Optional<Product> findById(UUID id) {
         return productRepository.findById(id);
    }

    @Override
    public boolean existsByName(String productName) {
        return productRepository.existsByName(productName);
    }

    @Override
    public void deleteById(UUID id) {
        productRepository.deleteById(id);
    }

    @Override
    public Optional<Product> findByName(String productName) {
        return Optional.ofNullable(productRepository.findByName(productName));
    }


    @Override
    public void deleteImage(ProductImage deletedImage) {
        // Delete the image from the image database
        imageService.deleteImage(deletedImage);

//        // Remove the image from the product
//        Product product = deletedImage.getProduct();
//        product.getImages().remove(deletedImage);
//        save(product);
    }

//    public void deleteImageById(UUID imageId) {
//        Optional<Image> image = imageRepository.findById(imageId);
//        if (image.isPresent()) {
//            Optional<Product> product = productRepository.findById(image.get().getProduct().getUuid());
//            if (product.isPresent()) {
//                product.get().getImages().removeIf(img -> img.getUuid().equals(imageId));
//                productRepository.save(product.get());
//            }
//            imageRepository.deleteById(imageId);
//        }
//    }

    @Override
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    @Override
    public Optional<Product> getProductById(UUID id) {
        return productRepository.findById(id);
    }

    @Override
    public Page<Product> findAll(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    @Override
    public Page<Product> findByName(Pageable pageable, String keyword) {
        return productRepository.findByName(pageable, keyword);
    }

    @Override
    public Page<Product> getByNamePaged(String keyword, Pageable pageable) {
        return productRepository.findByNameLike("%"+keyword+"%", pageable);
    }

	@Override
	public void deleteImageById(UUID imageId) {
		// TODO Auto-generated method stub
		
	}

	public Page<Product> findPaginated(int pageNo, int offset, String sortField,
			String sortDir, String searchTerm) {
		
		System.err.println(offset);
		Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() :
			Sort.by(sortField).descending();
		
	
		Pageable pageable = PageRequest.of(pageNo - 1, offset, sort);
		
		System.err.println(" end of product pagination service");
		return productRepository.findAll(searchTerm, pageable);
	}

	public boolean update(Product product) {
		Product existingProduct=  productRepository.findByName(product.getName());
	
		
	if(existingProduct!=null && !existingProduct.getUuid().toString().equals(product.getUuid().toString()) ) {
		System.err.println("name already exist.");
		return false;
	}
	existingProduct = productRepository.findById(product.getUuid()).get();
	 existingProduct.setMainImage(product.getMainImage());
	 existingProduct.setThumbnails(product.getThumbnails());
	 existingProduct.setName(product.getName());
	 existingProduct.setPrice(product.getPrice());
	 existingProduct.setCategory(product.getCategory());
	 existingProduct.setDescription(product.getDescription());
	 
	 productRepository.save(existingProduct);
       	
	 return true;

	}



}