package com.salesSavvy.app.adminserviceimplementaions;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.salesSavvy.app.adminservices.AdminBusinessServiceContract;
import com.salesSavvy.app.entities.Order;
import com.salesSavvy.app.entities.OrderItem;
import com.salesSavvy.app.entities.OrderStatus;
import com.salesSavvy.app.userrepositories.OrderItemRepository;
import com.salesSavvy.app.userrepositories.OrderRepository;
import com.salesSavvy.app.userrepositories.ProductRepository;

@Service
public class AdminBusinessService implements AdminBusinessServiceContract {
	
	private final OrderRepository orderRepository;
	private final OrderItemRepository orderItemRepository;
	private final ProductRepository productRepository;
		
	public AdminBusinessService(OrderRepository orderRepository, OrderItemRepository orderItemRepository,
			ProductRepository productRepository) {
		super();
		this.orderRepository = orderRepository;
		this.orderItemRepository = orderItemRepository;
		this.productRepository = productRepository;
	}

	@Override
	public Map<String, Object> calculateMonthlyBusiness(int month, int year) {
		List<Order> successfulOrder = orderRepository.findSuccessfulOrdersByMonthAndYear(month, year);
		
		return calculateBusinessmetrics(successfulOrder);
	}

	@Override
	public Map<String, Object> calculateDailyBusiness(LocalDate date) {
		List<Order> successfulOrder = orderRepository.findSuccessfulOrdersByDate(date);		
		return calculateBusinessmetrics(successfulOrder);
	}

	@Override
	public Map<String, Object> calculateYearlyBusiness(int year) {
		List<Order> successfulOrder = orderRepository.findSuccessfulOrdersByYear(year);		
		return calculateBusinessmetrics(successfulOrder);
	}

	@Override
	public Map<String, Object> calculateOverallBusiness() {
		List<Order> successfulOrder = orderRepository.findAllByStatusForOverallBusiness();		
		return calculateBusinessmetrics(successfulOrder);
	}
	
	
	private Map<String, Object> calculateBusinessmetrics(List<Order> orders) {
		double totalRevenue = 0.0;
		Map<String, Integer> categorySales = new HashMap<>();
		for (Order order : orders) {
			totalRevenue += order.getTotalAmount().doubleValue();
			
			
			List<OrderItem> items = orderItemRepository.findByOrderId(order.getOrderId());
			for (OrderItem item : items) {
				
				String categoryName = productRepository.findCategoryNameByProductId(item.getProductId());
				categorySales.put(categoryName, categorySales.getOrDefault(categoryName, 0) + item.getQuantity());
			}
		}
		Map<String, Object> metrics = new HashMap<>();
		metrics.put("totalRevenue", totalRevenue);
		metrics.put("categorySales", categorySales);
		return metrics;
	}
	

}
