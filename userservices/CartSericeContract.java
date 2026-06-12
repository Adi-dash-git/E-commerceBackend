package com.salesSavvy.app.userservices;

import java.util.Map;

import com.salesSavvy.app.entities.User;

public interface CartSericeContract {
	public void addToCart(User user, int productId, int quantity);
	public Map<String,Object>getCartItems(User authenticatedUser);
	
	public void updateCartItemQuantity(User authenticateduser, int productId, int quantity);
	public void deleteCartItem(int userid,int productId);
	public int countTotalItems(int userId);
	public int getCartItemCount(int userId); 
	
}
