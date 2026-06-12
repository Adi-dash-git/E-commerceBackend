package com.salesSavvy.app.userservices;

import java.util.Map;

import com.salesSavvy.app.entities.User;



public interface OrderServiceContract {
	 public Map<String, Object> getOrdersForUser(User user);
	
}
