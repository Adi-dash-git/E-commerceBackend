package com.salesSavvy.app.userserviceimplementations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.salesSavvy.app.entities.Cart_Items;
import com.salesSavvy.app.entities.Product;
import com.salesSavvy.app.entities.ProductImage;
import com.salesSavvy.app.entities.User;
import com.salesSavvy.app.userrepositories.CartRepository;
import com.salesSavvy.app.userrepositories.ProductImageRepository;
import com.salesSavvy.app.userrepositories.ProductRepository;
import com.salesSavvy.app.userrepositories.UserRepository;
import com.salesSavvy.app.userservices.CartSericeContract;

@Service
public class CartService implements CartSericeContract {

	
	private ProductRepository productRepository;
	private CartRepository cartRepository;
//	private Integer productId;
	private ProductImageRepository productImageRepository;
	private UserRepository userRepository;


	public CartService(ProductRepository productRepository, CartRepository cartRepository,
			ProductImageRepository productImageRepository, UserRepository userRepository) {
		super();
		this.productRepository = productRepository;
		this.cartRepository = cartRepository;
		this.productImageRepository = productImageRepository;
		this.userRepository = userRepository;
	}


	// Add an item to the cart
	public void addToCart(User user, int productId, int quantity) {

		
		Product product = productRepository.findById(productId)
				.orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + productId));

//		int userId;
		// Fetch cart item for this userId and productId
		Optional<Cart_Items> existingItem = cartRepository.findByUserAndProduct(user.getUserId(), productId);

		if (existingItem.isPresent()) {
			Cart_Items cartItem = existingItem.get();
			cartItem.setQuantity(cartItem.getQuantity() + quantity);
			cartRepository.save(cartItem);
		} else {
			Cart_Items newItem = new Cart_Items(user, product, quantity);
			cartRepository.save(newItem);
		}
	}





	@Override
	public Map<String, Object> getCartItems(User authenticatedUser) {
		List<Cart_Items> cartItems = cartRepository.findCartItemsWithProductDetails(authenticatedUser.getUserId());
		Map<String, Object> response = new HashMap();
		
		response.put("username", authenticatedUser.getUsername());
		response.put("role", authenticatedUser.getRole().toString());
		
		List<Map<String, Object>> products = new ArrayList();
		int overallTotalPrice = 0;
		
		for (Cart_Items cartItem : cartItems) {
			Map<String, Object> productDetails = new HashMap<>();

			// Get product details
			Product product = cartItem.getProduct();

			// Fetch product images from the ProductImageRepository
			List<ProductImage> productImages = productImageRepository.findByProduct_ProductId(product.getProductId());
		
//			String imageUrl = null;
//			if (productImages != null && !productImages.isEmpty()) {
//				// If there are images, get the first image's URL
//				imageUrl = productImages.get(0).getImageUrl();
//			} else {
//				// Set a default image if no images are available
//				imageUrl = "default-image-url";  // You can replace this with your default image URL
//			}
			
			String imageUrl = (productImages != null && !productImages.isEmpty()) ? productImages.get(0).getImageUrl() : "default-image-url";

			// Populate product details into the map
			productDetails.put("product_id", product.getProductId());
			productDetails.put("image_url", imageUrl);
			productDetails.put("name", product.getName());
			productDetails.put("description", product.getDescription());
			productDetails.put("price_per_unit", product.getPrice());
			productDetails.put("quantity", cartItem.getQuantity());
			productDetails.put("total_price", cartItem.getQuantity() * product.getPrice().doubleValue());

			// Add the product details to the products list
			products.add(productDetails);

			// Add to the overall total price
			overallTotalPrice += cartItem.getQuantity() * product.getPrice().doubleValue();
	
			
		}
		
		// Prepare the final cart response
		Map<String, Object> cart = new HashMap<>();
		cart.put("products", products);
		cart.put("overall_total_price", overallTotalPrice);

		// Add the cart details to the response
		response.put("cart", cart);

		return response;
	}
	
	
	
	
	// Update Cart Item Quantity
	public void updateCartItemQuantity(User authenticateduser, int productId, int quantity) {
		
		User ref = userRepository.findById(authenticateduser.getUserId()).orElseThrow(()-> new RuntimeException("user not found"));
		
		Product product = productRepository.findById(productId)
				.orElseThrow(() -> new IllegalArgumentException("Product not found"));

		
		// Fetch cart item for this userId and productId
		Optional<Cart_Items> existingItem = cartRepository.findByUserAndProduct(authenticateduser.getUserId(), productId);

		if (existingItem.isPresent()) {
			Cart_Items item = existingItem.get();
			if (quantity == 0) {
				deleteCartItem(authenticateduser.getUserId(), productId);
			} else {
				item.setQuantity(quantity);
				cartRepository.save(item);
			}
		} else {
			throw new RuntimeException("Cart item is not found associated with product and user");
		}	
		
	}
	public void deleteCartItem(int userid, int productId) {

//		User user = userRepository.findById(userid)
//				.orElseThrow(() -> new IllegalArgumentException("User not found"));

		Product product = productRepository.findById(productId)
				.orElseThrow(() -> new IllegalArgumentException("Product not found"));

		cartRepository.deleteCartItem(userid, productId);
	}


	@Override
	public int countTotalItems(int userId) {	
		 int count =cartRepository.countTotalItems(userId);
		 return count;
	}


	@Override
	public int getCartItemCount(int userId) {
		return cartRepository.countTotalItems(userId);			
	}
	

	
	
}
