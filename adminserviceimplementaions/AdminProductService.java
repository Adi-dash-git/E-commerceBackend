package com.salesSavvy.app.adminserviceimplementaions;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.salesSavvy.app.adminservices.AdminProductServiceContract;
import com.salesSavvy.app.entities.Category;
import com.salesSavvy.app.entities.Product;
import com.salesSavvy.app.entities.ProductImage;
import com.salesSavvy.app.userrepositories.CategoryRepository;
import com.salesSavvy.app.userrepositories.ProductImageRepository;
import com.salesSavvy.app.userrepositories.ProductRepository;

@Service
public class AdminProductService implements AdminProductServiceContract {
	
    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final CategoryRepository categoryRepository;
    
	public AdminProductService(ProductRepository productRepository, ProductImageRepository productImageRepository,
			CategoryRepository categoryRepository) {
		super();
		this.productRepository = productRepository;
		this.productImageRepository = productImageRepository;
		this.categoryRepository = categoryRepository;
	}

	@Override
	public Product addProductWithImage(String name, String description, Double price, Integer stock, Integer categoryId,
			String imageUrl) {
		// check if category exists using categoryId
		Optional<Category> category = categoryRepository.findById(categoryId);
		
		//if exist create product and add all the values to all attributes to product
		 if (category.isEmpty()) {
	            throw new IllegalArgumentException("Invalid category ID");
	      }
		 
		// save product
		 Product product = new Product(name, description, BigDecimal.valueOf(price), stock, category.get(), LocalDateTime.now(), LocalDateTime.now());
		 Product savedProduct = productRepository.save(product);
		
		//check if image url is null or balnk or empty		
		//if image url exists create productimage and set values to attributes and save productimage

		 if (imageUrl != null && !imageUrl.isEmpty()) {
	            ProductImage image = new ProductImage(savedProduct, imageUrl);
	            productImageRepository.save(image);
	        } else {
	            throw new IllegalArgumentException("Product image URL cannot be empty");
	        }

	        return savedProduct;
	}

	@Override
	public void deleteProduct(Integer productId) {
        // Check if the product exists
        if (!productRepository.existsById(productId)) {
            throw new IllegalArgumentException("Product not found");
        }

        // Delete associated product images
        productImageRepository.deleteByProductId(productId);

        // Delete the product
        productRepository.deleteById(productId);
    }


}
