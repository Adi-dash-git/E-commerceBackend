package com.salesSavvy.app.userserviceimplementations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.salesSavvy.app.entities.Category;
import com.salesSavvy.app.entities.Product;
import com.salesSavvy.app.entities.ProductImage;
import com.salesSavvy.app.userrepositories.CategoryRepository;
import com.salesSavvy.app.userrepositories.ProductImageRepository;
import com.salesSavvy.app.userrepositories.ProductRepository;



@Service
public class ProductService {

    private ProductRepository productRepository;
    private ProductImageRepository productImageRepository;
    private CategoryRepository categoryRepository;
    
    
    
    public ProductService() {
		// TODO Auto-generated constructor stub
	}
    
    
    
    @Autowired
    public ProductService(ProductRepository productRepository, ProductImageRepository productImageRepository,
			CategoryRepository categoryRepository) {
		super();
		this.productRepository = productRepository;
		this.productImageRepository = productImageRepository;
		this.categoryRepository = categoryRepository;
	}

	public List<Product> getProductsByCategory(String categoryName) {
        if (categoryName != null && !categoryName.isEmpty()) {
            Optional<Category> categoryOpt = categoryRepository.findByCategoryName(categoryName);
            if (categoryOpt.isPresent()) {
                Category category = categoryOpt.get();
                return productRepository.findByCategory_CategoryId(category.getCategoryId());
            } else {
                throw new RuntimeException("Category not found");
            }
        } else {
            return productRepository.findAll();
        }
    }

    public List<String> getProductImages(Integer productId) {
        List<ProductImage > productImages = productImageRepository.findByProduct_ProductId(productId);
        List<String> imageUrls = new ArrayList<>();
        for (ProductImage image : productImages) {
            imageUrls.add(image.getImageUrl());
        }
        return imageUrls;
    }
}
